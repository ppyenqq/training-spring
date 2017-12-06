package de.hska.lkit.demo.redis.repo.impl;

import java.util.List;

import de.hska.lkit.demo.redis.model.Post;
import de.hska.lkit.demo.redis.repo.PostRepository;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Repository;


@Repository
public class PostRepositoryImpl implements PostRepository{
	
		//private static final String KEY_SET_ALL_CONTENT 	= "all:usernames";

		//private static final String KEY_ZSET_ALL_USERNAMES 	= "all:usernames:sorted";
		
		private static final String KEY_HASH_ALL_PAST 		= "all:past";
		private static final String KEY_LIST_ALL_PAST 		= "all:list:past";
		
		private static final String KEY_PREFIX_PAST 	= "past:";
		private static final String KEY_LIST_ALL_CONTENT 		= "all:content";

		/**
		 * to generate unique ids for user/ ich glaube man benutzt diese Klasse anstatt normalen int ist
		 * weil wenn der Fal auftreten sollte, dass zwei Personen zu exact gleichem Zeitpunkt sich registriert,
		 * kann es passieren dass Sie beide den gleichen ID ekommen, während man mit dieser Klasse "atomic"/atomar 
		 * rechnet, das heißt die Rechnungen werden nacheinender ausgeführt, wenn der einer noch nicht fertig und
		 * gespeichert wurde kommt der nächste nicht dran, wenn ihr den kleinen Redis Tutorial von deren webseite
		 * gemacht habt, da wurde das mit dem atomar ja auch erwähnt in zusammenhang mit INCR-Operation
		 */
		private RedisAtomicLong postId;
		
		

		/**
		 * to save data in String format
		 */
		private StringRedisTemplate stringRedisTemplate;

		/**
		 * to save user data as object
		 */
		private RedisTemplate<String, Object> redisTemplate;
		
		

		/**
		 * hash operations for stringRedisTemplate
		 */
		private HashOperations<String, String, String> srt_hashOps;

		/**
		 * set operations for stringRedisTemplate
		 */
		private SetOperations<String, String> srt_setOps;
		
		/**
		 * zset operations for stringRedisTemplate
		 */
		private ZSetOperations<String, String> srt_zSetOps;
		private ListOperations<String, String> srt_listOps;
		
		
		

		/**
		 * hash operations for redisTemplate
		 */
		@Resource(name="redisTemplate")
		private HashOperations<String, String, Post> rt_hashOps;
		@Resource(name="redisTemplate")
		private ListOperations< String, Post> rt_listOps;
		
		
		/* 
		 * 
		 */
		@Autowired
		public PostRepositoryImpl(RedisTemplate<String, Object> redisTemplate, StringRedisTemplate stringRedisTemplate) {
			this.redisTemplate = redisTemplate;
			this.stringRedisTemplate = stringRedisTemplate;
			this.postId = new RedisAtomicLong("postId", stringRedisTemplate.getConnectionFactory());
		}

		/*
		 * man braucht die deklarationen um später Operationen aufzurufen um Daten in die Datenbank
		 * reinzusetzen oder was auch immer
		 * alternativ kann man es auch im Konstruktor deklarieren statt im init method 
		 */
		@PostConstruct
		private void init() {
			srt_hashOps = stringRedisTemplate.opsForHash();//Returns the operations performed on hash values.
			srt_setOps = stringRedisTemplate.opsForSet();//Returns the operations performed on set values.
			srt_zSetOps = stringRedisTemplate.opsForZSet();
			srt_listOps = stringRedisTemplate.opsForList();
		}

		
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * hska.iwi.vslab.repo.UserRepository#saveUser(hska.iwi.vslab.model.User)
		 */
		@Override
		public void savePost(Post post) {
			/* generate a unique id , jedes mal wenn ein neuer User geaddet wird, bekommt der nen ID, der eins höher ist 
			   als der User vorher, der vorherige Wert von dem vorherigen User wurde in Variable userid gespeichert,
			   und das +1 wird von der Methode incrementAndGet durchgeführt
			*/
			System.out.println("savePost 1");
			String id = String.valueOf(postId.incrementAndGet());

			post.setPostId(id);
			System.out.println("savePost2");
			// to show how objects can be saved
			// be careful, if username already exists it's not added another time
			//Hier wird der key indem man den user ablegt deklariert
			String key = KEY_PREFIX_PAST + id;
			System.out.println("savePost3");
			//man ruft die methode "put()" auf um Daten in das Hash zu speichern
			srt_hashOps.put(key, "postId", id);
			System.out.println("savePost4");
			srt_hashOps.put(key, "content", post.getContent());
			System.out.println("savePost5");
			srt_hashOps.put(key, "authorId", post.getAuthorId());
			srt_hashOps.put(key, "date", post.getDate());
			System.out.println("savePost6");
			// the key for a new user is added to the set for all usernames, hier werden alle Usernamen in Set gespeichert
			//srt_setOps.add(KEY_SET_ALL_CONTENT, past.getContent());
			
			// the key for a new user is added to the sorted set for all usernames
			//srt_zSetOps.add(KEY_ZSET_ALL_USERNAMES, past.getContent(), 0);
			
			// push in das globale timeline
			srt_listOps.leftPush(KEY_LIST_ALL_PAST, id);
			// push den content in den key von diesen author , vllt macht es mehr sinn id statt content reinzupushen???????????????
			srt_listOps.leftPush(KEY_LIST_ALL_PAST+post.getAuthorId(), post.getPostId());
			//Post die nur vom dem autor stammen, damit man auf seine "othershomee.html" nur seine post sieht
			srt_listOps.leftPush("post:only:from:"+post.getAuthorId(), post.getPostId());
			// to show how objects can be saved, hier wird ins redisTemplategespeichert nicht springTemplate,
			//wie im Configuration class erwähnt kann man in redisTemplate ganze Objekte speichern
			rt_hashOps.put(KEY_HASH_ALL_PAST, key, post);
			//rt_listOps.rightPush(KEY_LIST_ALL_PAST, past);

		}
		/*
		 * (non-Javadoc)
		 * @see de.hska.lkit.demo.redis.repo.PostRepository#getAllUsers()
		 * die Methode gibt mir alle User zurück mit deren informationen 
		 * die Methode .entries() des redisTemplate gibt mir alle User zurück die ich grad oben gespeichert habe als Map<S,U>
		 */
		@Override
		public Map<String, Post> getAllPost() {
			return rt_hashOps.entries(KEY_HASH_ALL_PAST);
		}
		/*
		 * (non-Javadoc)
		 * @see com.bee.maja.repo.PastRepository#getAllPastList()
		 * gibt die liste aller post-id die jemals gespeichert wurde , also die globle timeline
		 */
		@Override
		public List<String> getAllPostList() {
			return srt_listOps.range(KEY_LIST_ALL_PAST, 0, -1);
		}
		@Override
		public List<Post> getAllUsersPost(){
			List<Post> userPost = new LinkedList();
			for(long i=srt_listOps.size(KEY_LIST_ALL_PAST); i>0; i--) {
				//String postId = srt_listOps.index(KEY_LIST_ALL_PAST, i);
				//System.out.println("giving out postId: " + postId);
	           // userPost.add(srt_hashOps.get(KEY_PREFIX_PAST + Long.toString(i), "content"));
				Post post = new Post();
				
				post.setAuthorId(srt_hashOps.get(KEY_PREFIX_PAST + Long.toString(i), "authorId"));
				System.out.println(post.getAuthorId());
				post.setPostId(srt_hashOps.get(KEY_PREFIX_PAST + Long.toString(i), "postId"));
				System.out.println(post.getPostId());
				post.setContent(srt_hashOps.get(KEY_PREFIX_PAST + Long.toString(i), "content"));
				System.out.println(post.getContent());
				post.setDate(srt_hashOps.get(KEY_PREFIX_PAST + Long.toString(i), "date"));
				
				userPost.add(post);
	            
	        }
			return userPost;
			
		}
		//private Timeline eines user( zusammen mit Post von following liste)
		@Override
		public List<Post> getOneUsersPost(String authorname){
			List<Post> userPost = new LinkedList();
			for(long i=0; i<srt_listOps.size(KEY_LIST_ALL_PAST+authorname); i++) {
				String postId = srt_listOps.index(KEY_LIST_ALL_PAST+authorname, i);
				System.out.println("giving out postId: " + postId);
	           // userPost.add(srt_hashOps.get(KEY_PREFIX_PAST + Long.toString(i), "content"));
				Post post = new Post();
				
				post.setAuthorId(srt_hashOps.get(KEY_PREFIX_PAST + postId, "authorId"));
				System.out.println(post.getAuthorId());
				post.setPostId(srt_hashOps.get(KEY_PREFIX_PAST + postId, "postId"));
				post.setContent(srt_hashOps.get(KEY_PREFIX_PAST + postId, "content"));
				System.out.println(post.getContent());
				post.setDate(srt_hashOps.get(KEY_PREFIX_PAST + postId, "date"));
				
				userPost.add(post);
	            
	        }
			return userPost;
			
		}
		//private Timeline eines user( ohne Post von following liste)
				@Override
				public List<Post> getOnlyOneUsersPost(String authorname){
					List<Post> userPost = new LinkedList();
					for(long i=0; i<srt_listOps.size("post:only:from:"+authorname); i++) {
						String postId = srt_listOps.index("post:only:from:"+authorname, i);
						System.out.println("giving out postId: " + postId);
			           // userPost.add(srt_hashOps.get(KEY_PREFIX_PAST + Long.toString(i), "content"));
						Post post = new Post();
						
						post.setAuthorId(srt_hashOps.get(KEY_PREFIX_PAST + postId, "authorId"));
						System.out.println(post.getAuthorId());
						post.setPostId(srt_hashOps.get(KEY_PREFIX_PAST + postId, "postId"));
						post.setContent(srt_hashOps.get(KEY_PREFIX_PAST + postId, "content"));
						System.out.println(post.getContent());
						post.setDate(srt_hashOps.get(KEY_PREFIX_PAST + postId, "date"));
						
						userPost.add(post);
			            
			        }
					return userPost;
					
				}
		/*// ausgabe der content die gleichen author haben , die älteste reingepushte ist aber oben, muss daher bei timeline geändert werden
		@Override
		public List<String> getOneUsersPost22(String authorname){
			
			return srt_listOps.range(KEY_LIST_ALL_PAST+authorname, 0, -1);
			
		}*/

	

}

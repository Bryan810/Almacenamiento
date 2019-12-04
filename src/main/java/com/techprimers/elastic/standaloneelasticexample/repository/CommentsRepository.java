package com.techprimers.elastic.standaloneelasticexample.repository;

import com.techprimers.elastic.standaloneelasticexample.entity.Comments;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommentsRepository extends MongoRepository<Comments,String> {

//    Page<Comments> findAllData(Pageable pageable);
}

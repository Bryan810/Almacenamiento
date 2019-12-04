package com.techprimers.elastic.standaloneelasticexample.resource;

import com.techprimers.elastic.standaloneelasticexample.entity.Comments;
import com.techprimers.elastic.standaloneelasticexample.repository.CommentsRepository;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

@RestController
@RequestMapping("/rest/users")
public class UsersResource {

    TransportClient client;
    //    private CommentsService commentsService;
    private CommentsRepository repository;

    public UsersResource(CommentsRepository repository) throws UnknownHostException {
        client = new PreBuiltTransportClient(Settings.EMPTY)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
        this.repository = repository;
    }

    @GetMapping("/insert/{id}")
    public String insert(@PathVariable final String id) throws IOException {

        IndexResponse response = client.prepareIndex("employee", "id", id)
                .setSource(jsonBuilder()
                        .startObject()
                        .field("name", "Ajay")
                        .field("salary", 1200)
                        .field("teamName", "Development")
                        .endObject()
                )
                .get();
        return response.getResult().toString();
    }


    @GetMapping("/view/{id}")
    public Map<String, Object> view(@PathVariable final String id) {
        GetResponse getResponse = client.prepareGet("employee", "id", id).get();
        System.out.println(getResponse.getSource());


        return getResponse.getSource();
    }

    @GetMapping("/update/{id}")
    public String update(@PathVariable final String id) throws IOException {

        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index("employee")
                .type("id")
                .id(id)
                .doc(jsonBuilder()
                        .startObject()
                        .field("gender", "male")
                        .endObject());
        try {
            UpdateResponse updateResponse = client.update(updateRequest).get();
            System.out.println(updateResponse.status());
            return updateResponse.status().toString();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println(e);
        }
        return "Exception";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable final String id) {

        DeleteResponse deleteResponse = client.prepareDelete("employee", "id", id).get();

        System.out.println(deleteResponse.getResult().toString());
        return deleteResponse.getResult().toString();
    }

    @GetMapping("/comments")
    public void addComment() {
                Page<Comments> comments = repository.findAll(new PageRequest(0,1000000));
                comments.forEach(x -> {
                    try {
                        IndexResponse response = client.prepareIndex("comments", "id", x.getId())
                                .setSource(jsonBuilder()
                                                .startObject()
                                                .field("text", x.getText())
                                                .field("likes", x.getLikes())
                                                .endObject()
                                )
                                .get();
                        System.out.println(response.getResult().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        System.out.println("Fin");
    }
}

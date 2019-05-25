package com.dspa.project.activepoststatistics;

import com.dspa.project.activepoststatistics.repo.PostAndDateRepository;
import com.dspa.project.model.CommentEventStream;
import com.dspa.project.model.LikesEventStream;
import com.dspa.project.model.PostAndDate;
import com.dspa.project.model.PostEventStream;
import flink.*;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.flink.util.Collector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;

import java.io.Serializable;


@SpringBootApplication
@EntityScan(basePackages = {"com.dspa.project.model"})
@EnableJpaRepositories
public class ActivepoststatisticsApplication implements CommandLineRunner {


    @Autowired
    PostAndDateRepository repo;

    public static void main(String[] args) {
        //Spring
        ConfigurableApplicationContext context = SpringApplication.run(ActivepoststatisticsApplication.class, args);

        /**********     FLINK   START    *********************/

 //       doSomethingComment();
//        doSomethingLikes();
//        doSomethingPost();

 //       System.out.println("I work :D");
        /**********     FLINK   END    *********************/


        context.close();



    }
    @Override
    public void run(String... args) {

        StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
        CommentEventStreamConsumer consume = new CommentEventStreamConsumer();
        FlinkKafkaConsumer011<CommentEventStream> consumer = consume.createCommentEventStreamConsumer("comment","localhost:9092", "bar"); //TODO: change to correct topic
        consumer.setStartFromEarliest(); //TODO: change this based on what is required
        consumer.assignTimestampsAndWatermarks(new CommentEventStreamTimestampAssigner(Time.milliseconds(300000))); //TODO: check if it works


        DataStream<CommentEventStream> inputStream = environment.addSource(consumer);



        //StorePostAndDate test = new StorePostAndDate(repo);
        StorePostAndDate referenceName = (StorePostAndDate)SpringBeansUtil.getBean(StorePostAndDate.class);

        inputStream.map(referenceName);

        //repo.findAll().forEach(x -> System.out.println(x));;

//        PostAndDate test = new PostAndDate();
//        test.setId(0);
//        test.setLastUpdate(new Date());
//        System.out.println("Saving the value");
//        repo.save(test);
        try {
            environment.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**********************************************     FLINK       ************************************************/

    /**
     * TODO: Partially task 1. Do most of the code in the classe in the Flink package.
     * understand what groups are for and how to call them.
     * Ideally we should store the results back into another topic and handle there the windowing
     */
    public static void doSomethingComment(){
        StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
        CommentEventStreamConsumer consume = new CommentEventStreamConsumer();
        FlinkKafkaConsumer011<CommentEventStream> consumer = consume.createCommentEventStreamConsumer("comment","localhost:9092", "bar"); //TODO: change to correct topic
        consumer.setStartFromLatest(); //TODO: change this based on what is required
        consumer.assignTimestampsAndWatermarks(new CommentEventStreamTimestampAssigner(Time.milliseconds(300000))); //TODO: check if it works

        DataStream<CommentEventStream> inputStream = environment.addSource(consumer);
//        inputStream.map(new MapFunction<CommentEventStream, Long>(){
//                            @Override
//                            public Long map(CommentEventStream commentEventStream) throws Exception {
//                                //System.out.println(commentEventStream.toString());
//                                return commentEventStream.getSentAt().getTime();
//                            }
//                        }
//        ).print();
//                .timeWindowAll(Time.hours(24))
//                .addSink(flinkKafkaProducer);  //TODO: producer to send back some aggregated statistics

        //inputStream.flatMap(new Tokenizer()).keyBy(0).timeWindow(Time.milliseconds(50)).sum(1).print();

//        inputStream.keyBy(new KeySelector<CommentEventStream, Integer>() {
//            @Override
//            public Integer getKey(CommentEventStream commentEventStream) throws Exception {
//                return commentEventStream.getReply_to_postId();
//            }
//        }).timeWindow(Time.milliseconds(1000));

        //inputStream.flatMap(new SumCommentsByPostId()).keyBy(0).timeWindow(Time.milliseconds(1000)).sum(1).print(); //sum's up comments by post_id

        //inputStream.map(new StorePostId());


        try {
            environment.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static class Tokenizer implements FlatMapFunction<CommentEventStream, Tuple2<String, Integer>> {
        @Override
        public void flatMap(CommentEventStream commentEventStream, Collector<Tuple2<String, Integer>> collector) throws Exception {
            collector.collect(new Tuple2<>("comment",1));
        }
    }
    public static class SumCommentsByPostId implements FlatMapFunction<CommentEventStream, Tuple2<Integer, Integer>> {
        @Override
        public void flatMap(CommentEventStream commentEventStream, Collector<Tuple2<Integer, Integer>> collector) throws Exception {
            int postId = commentEventStream.getReply_to_postId();
            if(postId != -1){
                collector.collect(new Tuple2<>(postId,1));
            }
        }
    }
    public static class SumReplayToCommentsByCommentId implements FlatMapFunction<CommentEventStream, Tuple2<Integer, Integer>> {
        @Override
        public void flatMap(CommentEventStream commentEventStream, Collector<Tuple2<Integer, Integer>> collector) throws Exception {
            int commentId = commentEventStream.getReply_to_commentId();
            if(commentId != -1){
                collector.collect(new Tuple2<>(commentId,1));
            }
        }
    }
//    public class StorePost extends ProcessFunction<CommentEventStream, CommentEventStream> implements Serializable {
//
//        PostAndDateRepository repo;
//
//        public StorePost(PostAndDateRepository repo) {
//            this.repo = repo;
//        }
//
//        public void store(PostAndDate postAndDate){
//            repo.save(postAndDate);
//        }
//
//        @Override
//        public void processElement(CommentEventStream commentEventStream, Context context, Collector<CommentEventStream> collector) throws Exception {
//            System.out.println("Saving the value");
//            PostAndDate postAndDate = new PostAndDate();
//            postAndDate.setId(commentEventStream.getReply_to_postId());
//            postAndDate.setLastUpdate(commentEventStream.getSentAt());
//
//            store(postAndDate);
//        }
//    }
        public class StorePost implements Serializable {
        @Autowired
        PostAndDateRepository repo;

        public StorePost() {

        }

        public void store(PostAndDate postAndDate){
            repo.save(postAndDate);
        }

    }

    @Component
    @EntityScan(basePackages = {"com.dspa.project.model"})
    @Configurable
    public class StorePostId implements MapFunction<CommentEventStream, CommentEventStream>{

        @Override
        public CommentEventStream map(CommentEventStream commentEventStream) throws Exception {
//            System.out.println("Saving the value");
            PostAndDate postAndDate = new PostAndDate();
            postAndDate.setId(commentEventStream.getReply_to_postId());
            postAndDate.setLastUpdate(commentEventStream.getSentAt());
            StorePost storePost = new StorePost();
            storePost.store(postAndDate);
            return commentEventStream;
        }
    }

    public static void doSomethingLikes(){
        StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
        FlinkKafkaConsumer011<LikesEventStream> consumer = LikesEventStreamConsumer.createLikesEventStreamConsumer("likes","localhost:9092", "bar");
        consumer.setStartFromEarliest();
        consumer.assignTimestampsAndWatermarks(new LikesEventStreamTimestampAssigner()); //TODO: check if it works

        DataStream<LikesEventStream> inputStream = environment.addSource(consumer);
        inputStream.map(new MapFunction<LikesEventStream, String>(){
                            @Override
                            public String map(LikesEventStream likesEventStream) throws Exception {
                                System.out.println(likesEventStream.toString());
                                return likesEventStream.toString();
                            }
                        }
        );
//                .timeWindowAll(Time.hours(24))
//                .addSink(flinkKafkaProducer);  //TODO: producer to send back some aggregated statistics


        try {
            environment.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void doSomethingPost(){
        StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
        FlinkKafkaConsumer011<PostEventStream> consumer = PostEventStreamConsumer.createPostEventStreamConsumer("post", "localhost:9092", "bar");
        consumer.setStartFromEarliest();
        consumer.assignTimestampsAndWatermarks(new PostEventStreamTimestampAssigner()); //TODO: check if it works

        DataStream<PostEventStream> inputStream = environment.addSource(consumer);
        inputStream.map(new MapFunction<PostEventStream, String>(){
                            @Override
                            public String map(PostEventStream postEventStream) throws Exception {
                                System.out.println(postEventStream.toString()); //This is to test what you read from the topic. Make some statistics and print them like this. In a second moment we will look at how to save the results.
                                return postEventStream.toString();
                            }
                        }
        );
//                .timeWindowAll(Time.hours(24))
//                .addSink(flinkKafkaProducer);  //TODO: producer to send back some aggregated statistics
        try {
            environment.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
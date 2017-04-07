import com.mongodb.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


public class MongoTest {
    public MongoTest(String filename, String text) {
        MongoClient mongo = new MongoClient("localhost", 27017);
        DB db = mongo.getDB("admin");

        DBCollection table = db.getCollection("documents");
        BasicDBObject document = new BasicDBObject("tittle", filename).append("contebt", text);
        table.insert(document);
    }
}
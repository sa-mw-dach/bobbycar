package com.rh.ntt.db;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.DeleteResult;
import com.rh.ntt.model.AddGateway;
import com.rh.ntt.model.AddGatewayReport;
import com.rh.ntt.model.GatewayAnnounce;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MongoService {

    @Inject
    MongoClient mongoClient;

    public List<GatewayAnnounce> listAllGatewayAnnounces(){
        List<GatewayAnnounce> list = new ArrayList<GatewayAnnounce>();
        MongoCursor<Document> cursor = getGatewayAnnounceCollection().find().iterator();

        try {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                System.out.println(document);
                GatewayAnnounce an = new GatewayAnnounce();
                an.setGateway_id(document.getString("gateway_id"));
                an.setDomain(document.getString("domain"));
                an.setFirmware_ver(document.getString("firmware_ver"));
                an.setReport(document.getString("report"));
                an.setTimestamp(document.getDate("timestamp"));
                //an.setId(document.getObjectId("_id").toString());
                list.add(an);
                System.out.println(an);
            }
        } finally {
            cursor.close();
        }
        return list;
    }

    public void addGatewayAnnounce(@Valid GatewayAnnounce announce){
        Document document = new Document()
            .append("gateway_id", announce.getGateway_id())
            .append("report", Optional.ofNullable(announce.getReport()).orElse(""))
            .append("domain", Optional.ofNullable(announce.getDomain()).orElse(""))
            .append("firmware_ver", Optional.ofNullable(announce.getFirmware_ver()).orElse(""))
            .append("timestamp", Optional.ofNullable(announce.getTimestamp()).orElse(new Date()));
        getGatewayAnnounceCollection().insertOne(document);
    }

    public void deleteAllGatewayAnnounces() {
        DeleteResult result = getGatewayAnnounceCollection().deleteMany(new BasicDBObject());
        System.out.println("Entries deleted: "+result.getDeletedCount());
    }

    public void deleteGatewayAnnounceById(String gatewayId){
        BasicDBObject theQuery = new BasicDBObject();
        theQuery.put("gateway_id", gatewayId);

        DeleteResult result = getGatewayAnnounceCollection().deleteMany(theQuery);
        System.out.println("Entries deleted: "+result.getDeletedCount());
    }

    public void deleteGatewayAnnounceByObjectId(@NotEmpty String id){
        getGatewayAnnounceCollection().deleteOne(new Document("_id", new ObjectId(id)));
    }

    public long countGatewayAnnounces(){
        return getGatewayAnnounceCollection().countDocuments();
    }

    public void addGatewayMapping(@Valid AddGateway addGateway){
        Document document = new Document()
                .append("gateway_id", addGateway.getGateway_id())
                .append("UUID", addGateway.getGateway_uuid());
        getAddGatewayCollection().insertOne(document);
    }

    public void deleteGatewayMappingByUUID(String gatewayUUID){
        BasicDBObject theQuery = new BasicDBObject();
        theQuery.put("UUID", gatewayUUID);
        DeleteResult result = getAddGatewayCollection().deleteMany(theQuery);
        System.out.println("Entries deleted: "+result.getDeletedCount());
    }

    public String findGatewayIdByUUID(String gatewayUUID){
        BasicDBObject theQuery = new BasicDBObject();
        theQuery.put("UUID", gatewayUUID);

        String gwId = null;
        MongoCursor<Document> cursor = getAddGatewayCollection().find(theQuery).iterator();
        try {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                gwId = document.getString("gateway_id");
            }
        } finally {
            cursor.close();
        }
        return gwId;
    }

    public void addGatewayReport(@Valid AddGatewayReport gatewayReport){
        Document document = new Document()
                .append("gateway_UUID", gatewayReport.getGateway_UUID())
                .append("report", Optional.ofNullable(gatewayReport.getReport()).orElse(""))
                .append("msg_ID_ack", Optional.ofNullable(gatewayReport.getMsg_ID_ack()).orElse(Long.valueOf(0)))
                .append("status", Optional.ofNullable(gatewayReport.getStatus()).orElse(""))
                .append("name", Optional.ofNullable(gatewayReport.getName()).orElse(""))
                .append("description", Optional.ofNullable(gatewayReport.getDescription()).orElse(""))
                .append("location", Optional.ofNullable(gatewayReport.getLocation()).orElse(""))
                .append("timestamp", Optional.ofNullable(gatewayReport.getTimestamp()).orElse(new Date()));
        getGatewayReportCollection().insertOne(document);
    }

    public void deleteAllGatewayReports() {
        DeleteResult result = getGatewayReportCollection().deleteMany(new BasicDBObject());
        System.out.println("Entries deleted: "+result.getDeletedCount());
    }

    public void listDatabases(){
        List<Document> databases = mongoClient.listDatabases().into(new ArrayList<>());
        databases.forEach(db -> System.out.println(db.toJson()));
    }

    private MongoCollection getGatewayAnnounceCollection(){
        return mongoClient.getDatabase("ntt").getCollection("gateway-announce");
    }

    private MongoCollection getGatewayReportCollection(){
        return mongoClient.getDatabase("ntt").getCollection("gateway-report");
    }

    private MongoCollection getAddGatewayCollection(){
        return mongoClient.getDatabase("ntt").getCollection("gateway-mapping");
    }

}

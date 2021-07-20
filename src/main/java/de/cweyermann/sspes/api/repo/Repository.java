package de.cweyermann.sspes.api.repo;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class Repository {

    public List<Participant> getAllParticipants() {
        PaginatedScanList<Participant> scan = DynamoDb.mapper.scan(Participant.class, new DynamoDBScanExpression());
        return new ArrayList<>(scan);
    }
}
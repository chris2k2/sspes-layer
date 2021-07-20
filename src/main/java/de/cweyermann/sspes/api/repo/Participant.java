package de.cweyermann.sspes.api.repo;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@DynamoDBTable(tableName = "SSPS_Participants")
@AllArgsConstructor
@NoArgsConstructor
public class Participant {

    @DynamoDBHashKey
    private String name;

    private String url;

    private String lastChoice;

}

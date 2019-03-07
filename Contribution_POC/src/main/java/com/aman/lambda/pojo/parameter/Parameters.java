package com.aman.lambda.pojo.parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.stepfunctions.model.StartExecutionRequest;

/**
POJO for transferring data between Java based Lambda Functions.

AWS uses Jackson to serialise Java objects to JSON and vice versa
 * 
 * @author aman bansal
 */
public class Parameters {

    // S3 Key for uploaded snapshot
    private String uploadedFileName = null;

    // S3 Bucket containing uploaded snapshot
    private String s3BucketName = null;

    // Shall we send an alert email or not?
    private Boolean sendAlert = Boolean.FALSE;

    // Labels and confidence scores returned from AWS Rekognition
    private Map<String, Float> rekognitionLabels = null;

    // Location of snapshot after processing
    private String s3ArchivedKey = null;
    
    private StartExecutionRequest startExecutionRequest;
    // Step Function UUID
    private UUID stepFunctionID = null;
    
    S3Object s3Object = null;

    public Parameters() {}

    public Parameters(String s3BucketName, String uploadedFileName/*, UUID stepFunctionID*/) {
        this.s3BucketName = s3BucketName;
        this.uploadedFileName = uploadedFileName;
        //this.stepFunctionID = stepFunctionID;
    }
    
    public String getS3ArchivedKey() {
        return s3ArchivedKey;
    }

    public void setS3ArchivedKey(String s3ArchivedKey) {
        this.s3ArchivedKey = s3ArchivedKey;
    }

    public String getUploadedFileName() {
		return uploadedFileName;
	}

	public void setUploadedFileName(String uploadedFileName) {
		this.uploadedFileName = uploadedFileName;
	}

	public String getS3BucketName() {
		return s3BucketName;
	}

	public void setS3BucketName(String s3BucketName) {
		this.s3BucketName = s3BucketName;
	}

	public Boolean getSendAlert() {
        return sendAlert;
    }

    public void setSendAlert(Boolean sendAlert) {
        this.sendAlert = sendAlert;
    }

    public void setRekognitionLabels(Map<String, Float> rekognitionLabels) {
        this.rekognitionLabels = rekognitionLabels;
    }

    public Map<String, Float> getRekognitionLabels() {
        if (this.rekognitionLabels == null) {
            this.rekognitionLabels = new HashMap<String, Float>();
        }
        return this.rekognitionLabels;
    }

    public UUID getStepFunctionID(){
        return this.stepFunctionID;
    }
    
    public void setStepFunctionID(UUID id){
        this.stepFunctionID = id;
    }
    
    

    public String getS3Key() {
		return uploadedFileName;
	}

	public void setS3Key(String s3Key) {
		this.uploadedFileName = s3Key;
	}

	public S3Object getS3Object() {
		return s3Object;
	}

	
	public void setS3Object(S3Object s3Object) {
		this.s3Object = s3Object;
	}

	
	public StartExecutionRequest getStartExecutionRequest() {
		return startExecutionRequest;
	}

	public void setStartExecutionRequest(StartExecutionRequest startExecutionRequest) {
		this.startExecutionRequest = startExecutionRequest;
	}

	@Override
    public String toString() {
        return "Parameters [s3Object="+s3Object+", s3Key=" + uploadedFileName + ", s3Bucket=" + s3BucketName + ", sendAlert=" + sendAlert
                + ", rekognitionLabels=" + rekognitionLabels + ", s3ArchivedKey=" + s3ArchivedKey + ", stepFunctionID="
                + stepFunctionID + "]";
    }
}
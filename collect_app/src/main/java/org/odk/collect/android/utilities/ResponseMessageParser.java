package org.odk.collect.android.utilities;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import timber.log.Timber;

public class ResponseMessageParser {

    private static final String MESSAGE_XML_TAG = "message";
    private static final String FIELDSIGHT_INSTANCE_ID_XML_TAG = "finstanceID";


    private boolean isValid;
    private String messageResponse;
    public String fieldSightInstanceId;

    public boolean isValid() {
        return this.isValid;
    }

    public String getFieldSightInstanceId() {
        return this.fieldSightInstanceId;
    }

    public String getMessageResponse() {
        return messageResponse;
    }

    public void setMessageResponse(String response) {
        isValid = false;
        try {
            if (response.contains("OpenRosaResponse")) {
                DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document doc = builder.parse(new ByteArrayInputStream(response.getBytes()));
                doc.getDocumentElement().normalize();

                if (doc.getElementsByTagName(MESSAGE_XML_TAG).item(0) != null) {
                    messageResponse = doc.getElementsByTagName(MESSAGE_XML_TAG).item(0).getTextContent();
                    isValid = true;
                }
            }

            this.fieldSightInstanceId = parseForFieldSightInstanceId(response);

        } catch (SAXException | IOException | ParserConfigurationException e) {
            Timber.e(e, "Error parsing XML message due to %s ", e.getMessage());
        }
    }

    private String parseForFieldSightInstanceId(String response) {
        String fieldSightInstanceId = null;
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;

        try {
            builder = dbFactory.newDocumentBuilder();
            Document doc = null;

            if (response.contains("OpenRosaResponse")) {
                doc = builder.parse(new ByteArrayInputStream(response.getBytes()));
                doc.getDocumentElement().normalize();

                NodeList nList = doc.getElementsByTagName("submissionMetadata");
                try {
                    fieldSightInstanceId = ((Element) nList.item(0)).getAttribute(FIELDSIGHT_INSTANCE_ID_XML_TAG);
                } catch (Exception e) {
                    isValid = false;
                    Timber.e(e, "Failed to retrive fieldsight submission id from submission sucess xml");
                }

            }

            return fieldSightInstanceId;

        } catch (SAXException | IOException | ParserConfigurationException e) {
            Timber.e(e, "Error parsing XML message due to %s ", e.getMessage());
            isValid = false;
        }

        return fieldSightInstanceId;
    }


}

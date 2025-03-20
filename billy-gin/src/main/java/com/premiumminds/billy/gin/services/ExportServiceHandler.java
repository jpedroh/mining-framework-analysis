package com.premiumminds.billy.gin.services;
import java.io.OutputStream;
import com.premiumminds.billy.gin.services.exceptions.ExportServiceException;

public interface ExportServiceHandler {
  public <T extends ExportServiceRequest> void export(T request, OutputStream targetStream) throws ExportServiceException;
}
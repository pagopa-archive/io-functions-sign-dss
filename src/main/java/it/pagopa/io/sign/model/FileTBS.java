package it.pagopa.io.sign.model;

import it.pagopa.io.sign.utility.FileUtility;
import java.io.File;

public class FileTBS {

  private File file;
  private String hash;
  private String signedHash;
  private String size;
  private Boolean signed = false;

  public FileTBS(String filePath) {
    File tmpFile = new File(filePath);
    this.file = tmpFile;
    this.size = FileUtility.calculateSize(this.file);
  }

  public FileTBS(File tmpFile) {
    this.file = tmpFile;
    this.size = FileUtility.calculateSize(this.file);
  }

  public File getFile() {
    return this.file;
  }

  public String getHash() {
    return hash;
  }

  public String getSignedHash() {
    return signedHash;
  }

  public String getSize() {
    return size;
  }

  public Boolean isSigned() {
    return signed;
  }

  public void setHash(String hash) {
    this.hash = hash;
  }

  public void setSignedHash(String signedHash) {
    this.signedHash = signedHash;
  }

  public void setSigned(Boolean signed) {
    this.signed = signed;
  }
}

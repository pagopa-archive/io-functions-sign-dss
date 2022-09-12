package it.pagopa.io.sign;

import javax.naming.ConfigurationException;

public final class Configuration {

  public String signatureFieldId;
  public String signatureFieldTxt;

  private Configuration() throws ConfigurationException {
    signatureFieldId = System.getenv("SignatureFieldId");
    if (signatureFieldId == null) {
      throw new ConfigurationException("SignatureFieldId not set!");
    }
    signatureFieldTxt = System.getenv("SignatureFieldTxt");
  }

  private static class ConfigurationHelper {

    private static final Configuration INSTANCE;

    static {
      try {
        INSTANCE = new Configuration();
      } catch (Exception e) {
        throw new ExceptionInInitializerError(e);
      }
    }
  }

  public static Configuration getInstance() {
    return ConfigurationHelper.INSTANCE;
  }
}

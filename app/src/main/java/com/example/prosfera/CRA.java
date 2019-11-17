package com.example.prosfera;

public class CRA {
    private String websiteAddress;
    private String StmtOfOfficialReceipt;

    // TODO: Should add a comment about why this class is needed
    public CRA(String webAdd)
    {
        this.websiteAddress = webAdd;
        this.StmtOfOfficialReceipt = "This is an official receipt.";
    }

    public String getWebsiteAddress() { return websiteAddress; }

    public void setWebsiteAddress(String wa) { websiteAddress = wa; }
}

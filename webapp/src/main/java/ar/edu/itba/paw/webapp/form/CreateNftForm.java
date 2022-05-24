package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validators.interfaces.ImageConstraint;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;

public class CreateNftForm {
    @Digits(integer = 15, fraction = 0)
    @Min(value=0L)
    @NotNull
    private int nftId;

    @NotBlank
    @Pattern(regexp = "^0x[a-fA-F0-9]{40}$")
    private String contractAddr;

    @NotBlank
    @Size(min = 1, max = 30)
    @Pattern(regexp = "[a-zA-Z0-9 ]+")
    private String name;

    @NotBlank
    private String chain;

    @ImageConstraint(maxSizeMB = 5)
    private MultipartFile image;

    @NotBlank
    @Size(min = 1, max = 30)
    @Pattern(regexp = "[a-zA-Z0-9 ]+")
    private String collection;

    private String description;

    private String[] properties;

    public int getNftId() {
        return nftId;
    }

    public void setNftId(int nftId) {
        this.nftId = nftId;
    }

    public String getContractAddr() {
        return contractAddr;
    }

    public void setContractAddr(String contractAddr) {
        this.contractAddr = contractAddr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChain() {
        return chain;
    }

    public void setChain(String chain) {
        this.chain = chain;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getProperties() {
        return properties;
    }

    public void setProperties(String[] properties) {
        this.properties = properties;
    }

}

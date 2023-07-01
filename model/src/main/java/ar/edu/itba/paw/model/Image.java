package ar.edu.itba.paw.model;

import javax.persistence.*;

@Entity
@Table(name = "images")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "images_id_image_seq")
    @SequenceGenerator(allocationSize = 1, sequenceName = "images_id_image_seq", name = "images_id_image_seq")
    @Column(name = "id_image")
    private Integer idImage;

    @Column(name = "image", nullable = false)
    @Basic(fetch = FetchType.LAZY)
    private byte[] image;

    /* default */ Image() {
        // just for hibernate
    }

    public Image(int idImage, byte[] image){
        this.idImage = idImage;
        this.image = image;
    }

    public Image(byte[] image) {
        this.image = image;
    }

    public int getIdImage() {
        return idImage;
    }

    public byte[] getImage() {
        return image;
    }

}

/*
package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Image;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.*;
import static ar.edu.itba.paw.persistence.Utils.IMAGE_TABLE;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class ImageJdbcDaoTest {

    private final MultipartFile image = new MockMultipartFile("image", new byte[1]);

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert imageJdbcInsert;
    private ImageJdbcDao imageJdbcDao;

    @Autowired
    private DataSource ds;

    @Before
    public void setUp() {
        imageJdbcDao = new ImageJdbcDao(ds);
        jdbcTemplate = new JdbcTemplate(ds);
        imageJdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName(IMAGE_TABLE)
                .usingGeneratedKeyColumns("id_image");

        JdbcTestUtils.deleteFromTables(jdbcTemplate, IMAGE_TABLE);
    }

    @Test
    public void testCreateImage() {
        imageJdbcDao.createImage(image);

        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, IMAGE_TABLE));
    }

    @Test
    public void testGetImageById() {
        int imageId = imageJdbcInsert.executeAndReturnKey(Collections.singletonMap("image", new byte[1])).intValue();

        Optional<Image> image = imageJdbcDao.getImage(imageId);

        assertTrue(image.isPresent());
        assertEquals(1,image.get().getImage().length);
        assertEquals(0,image.get().getImage()[0]);
    }


}
*/

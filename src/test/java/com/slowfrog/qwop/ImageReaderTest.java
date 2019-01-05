package com.slowfrog.qwop;

import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class ImageReaderTest {

    @Test
    public void readImages() throws IOException {
        System.out.println(this.getClass().getResource("/").getPath());

        final InputStream resourceAsStream = this.getClass().getResourceAsStream("src/test/resources/end.png");
        System.out.println(resourceAsStream);
//        BufferedImage img = ImageIO.read(this.getClass().getResourceAsStream("./dist_11.2.png"));
//        float result = ImageReader.readDistance(img);
//        assertThat(result, equalTo(11.2f));
    }
}

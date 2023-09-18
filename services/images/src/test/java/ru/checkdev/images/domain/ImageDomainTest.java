package ru.checkdev.images.domain;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author olegbelov
 * @since 17.11.2016
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ImageDomainTest {

	@Test
	public void imageDefaultCreationTest() {
		Image image = new Image();
		assertNotNull(image);
	}
	
	@Test
	public void imageCreationTest() {
		Image image = new Image("testFileName",
				"testDescription",
				new byte[] {1, 2, 3});
		assertNotNull(image);
	}

}

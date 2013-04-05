package de.incompleteco.spring.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import javax.annotation.Resource;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import de.incompleteco.spring.jpa.domain.SimpleEntity;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({"classpath:/META-INF/spring/*-context.xml"})
@ActiveProfiles("junit")
public class IntegrationTest {

	@Resource
	private WebApplicationContext context;
	
	private ObjectMapper mapper;
	
	private MockMvc mvc;
	
	@Before
	public void before() throws Exception {
		//setup
		mapper = new ObjectMapper();//json
		mvc = webAppContextSetup(context).build();		
	}

	@Test
	public void test() throws Exception {
		//create an object
		SimpleEntity entity = new SimpleEntity();
		entity.setStuff("hello world");
		//convert
		String json = mapper.writeValueAsString(entity);
		//execute
		MvcResult result = mvc.perform(put("/simpleEntity").contentType(MediaType.APPLICATION_JSON).content(json))
			.andExpect(status().isOk()).andReturn();
		//now convert back
		SimpleEntity resultEntity = mapper.readValue(result.getResponse().getContentAsByteArray(), SimpleEntity.class);
		//now check
		assertTrue(resultEntity.getId() > 0);
		assertEquals(entity.getStuff(),resultEntity.getStuff());
		//retrieve by the id
		result = mvc.perform(get("/simpleEntity").param("id", resultEntity.getId().toString()))
			.andExpect(status().isOk()).andReturn();
		//now convert
		SimpleEntity retrievedEntity = mapper.readValue(result.getResponse().getContentAsByteArray(), SimpleEntity.class);
		//now check
		assertEquals(entity.getStuff(),retrievedEntity.getStuff());
		//retrieve all
		result = mvc.perform(get("/simpleEntity"))
				.andExpect(status().isOk()).andReturn();
		//now it *should* be an array of SimpleEntity
		SimpleEntity[] entities = mapper.readValue(result.getResponse().getContentAsByteArray(),SimpleEntity[].class);
		//test
		assertNotNull(entities);
		assertTrue(entities.length > 0);
		
	}
}

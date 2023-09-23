package com.fabrizio.tesi.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import com.fabrizio.tesi.rest.common.dto.ResponseTable;
import com.fabrizio.tesi.rest.crud.CRUDController;
import com.fabrizio.tesi.rest.crud.dto.TableResponseDTO;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("dev")
@TestMethodOrder(OrderAnnotation.class)
class CrudControllerTest {
	@Autowired
	CRUDController controller;

	@Test
	@Order(1)
	void getListInvalidFilter() {
		try {
			controller.listElem("", Map.of());
		} catch (ResponseStatusException e) {
			assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
		}
	}

	@Test
	@Order(2)
	void getListEmptyFilterAsAnalist() {
		ResponseTable<TableResponseDTO> list = controller.listElem("{}", Map.of());
		assertNotNull(list);
		assertNotEquals(list.getTotal(), 0);
		assertNotNull(list.getPageSizeOptions());
		assertFalse(list.getPageSizeOptions().isEmpty());
		assertNotNull(list.getData());
		assertFalse(list.getData().isEmpty());
		for (TableResponseDTO data : list.getData()) {
			assertNotNull(data);
			assertTrue(data.isReadOnly());
			assertTrue(data.isView());
			assertFalse(data.isEdit());
			assertFalse(data.isDelete());
		}
	}

	@Test
	@Order(3)
	void getListEmptyFilterAsAdmin() {
		ResponseTable<TableResponseDTO> list = controller.listElem("{}", Map.of("role", "admin"));
		assertNotNull(list);
		assertNotEquals(list.getTotal(), 0);
		assertNotNull(list.getPageSizeOptions());
		assertFalse(list.getPageSizeOptions().isEmpty());
		assertNotNull(list.getData());
		assertFalse(list.getData().isEmpty());
		for (TableResponseDTO data : list.getData()) {
			assertNotNull(data);
			assertFalse(data.isReadOnly());
			assertTrue(data.isView());
			assertTrue(data.isEdit());
			assertTrue(data.isDelete());
		}
	}

	@Test
	@Order(4)
	void getListFilteredAsAdmin() {
		String json="{"
				+ "    \"name\": \"pro\","
				+ "    \"description\": \"desc\","
				+ "    \"function\": \"sin\","
				+ "    \"agentId\": \"a\""
				+ "}";
		ResponseTable<TableResponseDTO> list = controller.listElem(json, Map.of("role", "admin"));
		assertNotNull(list);
		assertEquals(list.getTotal(), 1);
		assertNotNull(list.getPageSizeOptions());
		assertFalse(list.getPageSizeOptions().isEmpty());
		assertNotNull(list.getData());
		assertFalse(list.getData().isEmpty());
		for (TableResponseDTO data : list.getData()) {
			assertNotNull(data);
			assertEquals(data.getId(), 0);
			assertFalse(data.isReadOnly());
			assertTrue(data.isView());
			assertTrue(data.isEdit());
			assertTrue(data.isDelete());
		}
	}
		
	@Test
	@Order(6)
	void getListSortedAscendAsAdmin() {
		String json="{"
				+ "    \"name\": \"\","
				+ "    \"description\": \"\","
				+ "    \"function\": \"\","
				+ "    \"agentId\": \"\","
				+ "    \"sort\": {\"name\": \"ascend\"}"
				+ "}";
		ResponseTable<TableResponseDTO> list = controller.listElem(json, Map.of("role", "admin"));
		assertNotNull(list);
		assertNotEquals(list.getTotal(), 0);
		assertNotNull(list.getPageSizeOptions());
		assertFalse(list.getPageSizeOptions().isEmpty());
		assertNotNull(list.getData());
		assertFalse(list.getData().isEmpty());
		for (TableResponseDTO data : list.getData()) {
			assertNotNull(data);
			assertFalse(data.isReadOnly());
			assertTrue(data.isView());
			assertTrue(data.isEdit());
			assertTrue(data.isDelete());
		}
	}
	
	@Test
	@Order(7)
	void getListSortedDescentAsAdmin() {
		String json="{"
				+ "    \"name\": \"\","
				+ "    \"description\": \"\","
				+ "    \"function\": \"\","
				+ "    \"agentId\": \"\","
				+ "    \"sort\": {\"name\": \"descend\"}"
				+ "}";
		ResponseTable<TableResponseDTO> list = controller.listElem(json, Map.of("role", "admin"));
		assertNotNull(list);
		assertNotEquals(list.getTotal(), 0);
		assertNotNull(list.getPageSizeOptions());
		assertFalse(list.getPageSizeOptions().isEmpty());
		assertNotNull(list.getData());
		assertFalse(list.getData().isEmpty());
		for (TableResponseDTO data : list.getData()) {
			assertNotNull(data);
			assertFalse(data.isReadOnly());
			assertTrue(data.isView());
			assertTrue(data.isEdit());
			assertTrue(data.isDelete());
		}
	}

	@Test
	@Order(8)
	void getListSortedFaultedAsAdmin() {
		String json="{"
				+ "    \"name\": \"\","
				+ "    \"description\": \"\","
				+ "    \"function\": \"\","
				+ "    \"agentId\": \"\","
				+ "    \"sort\": {\"name\": \"\"}"
				+ "}";
		ResponseTable<TableResponseDTO> list = controller.listElem(json, Map.of("role", "admin"));
		assertNotNull(list);
		assertNotEquals(list.getTotal(), 0);
		assertNotNull(list.getPageSizeOptions());
		assertFalse(list.getPageSizeOptions().isEmpty());
		assertNotNull(list.getData());
		assertFalse(list.getData().isEmpty());
		for (TableResponseDTO data : list.getData()) {
			assertNotNull(data);
			assertFalse(data.isReadOnly());
			assertTrue(data.isView());
			assertTrue(data.isEdit());
			assertTrue(data.isDelete());
		}
	}

	@Test
	@Order(9)
	void getDetails() {
		TableResponseDTO detail = controller.getElem(0);

		assertNotNull(detail);
		assertEquals(0, detail.getId());
	}

	@Test
	@Order(10)
	void getNotExistingDetails() {
		try {
			controller.getElem(-1);
		} catch (ResponseStatusException e) {
			assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
		}
	}

	@Test
	@Order(11)
	void saveDetailAsAnalist() {
		ResponseEntity<TableResponseDTO> response = controller.saveElem(new TableResponseDTO(), Map.of());
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}

	@Test
	@Order(12)
	void saveNotExistingDetailAsAdmin() {
		TableResponseDTO test = new TableResponseDTO();
		test.setId(10000);
		try {
			controller.saveElem(test, Map.of("role", "admin"));
		} catch (ResponseStatusException e) {
			assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
		}
	}

	@Test
	@Order(13)
	void saveExistingDetailIncompleteAsAdmin() {
		TableResponseDTO test = new TableResponseDTO();
		test.setId(1);
//		test.setName("prova 2");
		test.setDescription("test");
//		test.setAmplitude(-50);
//		test.setFrequency(32.9);
//		test.setFunction("tan");
//		test.setAgentId("jfdmbm");

		ResponseEntity<TableResponseDTO> response = controller.saveElem(test, Map.of("role", "admin"));
		assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
	}

	@Test
	@Order(14)
	void saveExistingDetailAsAdmin() {
		TableResponseDTO test = new TableResponseDTO();
		test.setId(1);
		test.setName("prova 2");
		test.setDescription("test");
		test.setFunction("tan");
		test.setAgentId("jfdmbm");
		ResponseEntity<TableResponseDTO> response = controller.saveElem(test, Map.of("role", "admin"));
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.hasBody());
		assertNotNull(response.getBody());
		assertEquals(test.getId(), response.getBody().getId());
		assertEquals(test.getDescription(), response.getBody().getDescription());
		assertEquals(test.getName(), response.getBody().getName());
		assertEquals(test.getAgentId(), response.getBody().getAgentId());
		assertEquals(test.getFunction(), response.getBody().getFunction());
	}

	@Test
	@Order(15)
	void saveNewConflictingDetailAsAdmin() {
		TableResponseDTO test = new TableResponseDTO();
		test.setId(-1);
		test.setName("prova 1");
		test.setDescription("test");
		test.setFunction("tan");
		test.setAgentId("jfdmbm");
		ResponseEntity<TableResponseDTO> response = controller.saveElem(test, Map.of("role", "admin"));
		assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
		assertTrue(response.hasBody());
		assertNotNull(response.getBody());
		assertNotNull(test.getId());
	}


	@Test
	@Order(16)
	void saveNewConflictingDetailAsAdmin2() {
		TableResponseDTO test = new TableResponseDTO();
		test.setId(-1);
		test.setName("prova 10");
		test.setDescription("test");
		test.setFunction("tan");
		test.setAgentId("a");
		ResponseEntity<TableResponseDTO> response = controller.saveElem(test, Map.of("role", "admin"));
		assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
		assertTrue(response.hasBody());
		assertNotNull(response.getBody());
		assertNotNull(test.getId());
	}

	@Test
	@Order(17)
	void saveNewDetailAsAdmin() {
		TableResponseDTO test = new TableResponseDTO();
		test.setId(-1);
		test.setName("prova 10");
		test.setDescription("test");
		test.setFunction("tan");
		test.setAgentId("ab");
		ResponseEntity<TableResponseDTO> response = controller.saveElem(test, Map.of("role", "admin"));
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.hasBody());
		assertNotNull(response.getBody());
		assertNotEquals(test.getId(), response.getBody().getId());
		assertEquals(test.getDescription(), response.getBody().getDescription());
		assertEquals(test.getName(), response.getBody().getName());
		assertEquals(test.getAgentId(), response.getBody().getAgentId());
		assertEquals(test.getFunction(), response.getBody().getFunction());
	}

	@Test
	@Order(18)
	void deleteAsAnalist() {
		ResponseEntity<Void> response = controller.deleteElem(0, Map.of());
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}

	@Test
	@Order(19)
	void deleteNotExistingAsAdmin() {
		ResponseEntity<Void> response = controller.deleteElem(-1, Map.of("role", "admin"));
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	@Test
	@Order(20)
	void deleteExistingAsAdmin() {
		ResponseEntity<Void> response = controller.deleteElem(0, Map.of("role", "admin"));
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	@Order(21)
	void toggleAsAnalist() {
		ResponseEntity<Void> response = controller.toggleValue("", false, Map.of(), Map.of());
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		response = controller.toggleValue("", false, Map.of("id", 6L), Map.of());
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}

	@Test
	@Order(22)
	void toggleInvalidPropAsAdmin() {
		ResponseEntity<Void> response = controller.toggleValue("", false, null, Map.of("role", "admin"));
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		response = controller.toggleValue("non_esisto", false, Map.of("id", 6L), Map.of("role", "admin"));
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		response = controller.toggleValue("name", false, Map.of("id", 6L), Map.of("role", "admin"));
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	@Order(23)
	void toggleAsAdmin() {
		ResponseEntity<Void> response = controller.toggleValue("enable", false, null, Map.of("role", "admin"));
		assertEquals(HttpStatus.OK, response.getStatusCode());

		ResponseTable<TableResponseDTO> list = controller.listElem("{}", Map.of());
		assertNotNull(list);
		assertNotEquals(list.getTotal(), 0);
		assertNotNull(list.getPageSizeOptions());
		assertFalse(list.getPageSizeOptions().isEmpty());
		assertNotNull(list.getData());
		assertFalse(list.getData().isEmpty());
		for (TableResponseDTO data : list.getData()) {
			assertNotNull(data);
			assertFalse(data.isEnable());
		}
		
		response = controller.toggleValue("enable", true, Map.of("id", 1L), Map.of("role", "admin"));
		assertEquals(HttpStatus.OK, response.getStatusCode());
		
		String json="{"
				+ "    \"name\": \"prova 2\","
				+ "    \"description\": \"\","
				+ "    \"function\": \"\","
				+ "    \"agentId\": \"\""
				+ "}";
		list = controller.listElem(json, Map.of());
		assertNotNull(list);
		assertNotEquals(list.getTotal(), 0);
		assertNotNull(list.getPageSizeOptions());
		assertFalse(list.getPageSizeOptions().isEmpty());
		assertNotNull(list.getData());
		assertFalse(list.getData().isEmpty());
		for (TableResponseDTO data : list.getData()) {
			assertNotNull(data);
			assertTrue(data.isEnable());
		}
	}

	@Test
	@Order(24)
	void userAllowedTest() {
		boolean response = controller.userAllowedEdit(null);
		assertFalse(response);
		response = controller.userAllowedEdit(Map.of());
		assertFalse(response);
		response = controller.userAllowedEdit(Map.of("test", "finto"));
		assertFalse(response);
		response = controller.userAllowedEdit(Map.of("role", "ADMIN"));
		assertTrue(response);
	}

}

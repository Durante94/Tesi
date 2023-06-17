package com.fabrizio.tesi.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import com.fabrizio.tesi.rest.common.dto.ResponseTable;
import com.fabrizio.tesi.rest.crud.CRUDController;
import com.fabrizio.tesi.rest.crud.dto.TableResponseDTO;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("dev")
class CrudControllerTest {
	@Autowired
	CRUDController controller;

	@Test
	void getListInvalidFilter() {
		try {
			controller.listElem("", Map.of());
		} catch (ResponseStatusException e) {
			assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
		}
	}

	@Test
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
	void getListFilteredAsAdmin() {
		String json="{"
				+ "    \"name\": \"pro\","
				+ "    \"description\": \"desc\","
				+ "    \"amplitude\": 1,"
				+ "    \"frequency\": 9,"
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
	void getListFiltereWithFloatingPointNumdAsAdmin() {
		String json="{"
				+ "    \"name\": \"\","
				+ "    \"description\": \"\","
				+ "    \"amplitude\": 5.6,"
				+ "    \"frequency\": 4.3,"
				+ "    \"function\": \"\","
				+ "    \"agentId\": \"\""
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
			assertEquals(data.getId(), 4);
			assertFalse(data.isReadOnly());
			assertTrue(data.isView());
			assertTrue(data.isEdit());
			assertTrue(data.isDelete());
		}
	}
	
	@Test
	void getListSortedAscendAsAdmin() {
		String json="{"
				+ "    \"name\": \"\","
				+ "    \"description\": \"\","
				+ "    \"amplitude\": 0,"
				+ "    \"frequency\": 0,"
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
	void getListSortedDescentAsAdmin() {
		String json="{"
				+ "    \"name\": \"\","
				+ "    \"description\": \"\","
				+ "    \"amplitude\": 0,"
				+ "    \"frequency\": 0,"
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
	void getListSortedFaultedAsAdmin() {
		String json="{"
				+ "    \"name\": \"\","
				+ "    \"description\": \"\","
				+ "    \"amplitude\": 0,"
				+ "    \"frequency\": 0,"
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
}

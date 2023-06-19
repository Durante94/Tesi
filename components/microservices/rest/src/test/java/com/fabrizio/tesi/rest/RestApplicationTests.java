package com.fabrizio.tesi.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.fabrizio.tesi.rest.common.dto.ResponseTable;
import com.fabrizio.tesi.rest.crud.dto.TableResponseDTO;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("dev")
class RestApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void coverageTest() {
		RestApplication.main(new String[] {});

		List<TableResponseDTO> list = new ArrayList<>();
		for (int i = 0; i < 101; i++) {
			TableResponseDTO mock = new TableResponseDTO();
			list.add(mock);
		}
		ResponseTable<TableResponseDTO> coverage = new ResponseTable<>(list.size(), list);

		assertEquals(coverage.getTotal(), list.size());
		assertEquals(coverage.getPageSizeOptions().size(), 5);
		assertTrue(list.equals(coverage.getData()));
	}
}

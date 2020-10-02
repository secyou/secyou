package com.busicomjp.sapp.repository.a;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.busicomjp.sapp.dto.a.JournalEntryDto;
import com.busicomjp.sapp.model.a.JournalEntryData;

@Repository
public interface A2Repository {
	
	List<JournalEntryData> selectJournalList(JournalEntryDto journal);
}

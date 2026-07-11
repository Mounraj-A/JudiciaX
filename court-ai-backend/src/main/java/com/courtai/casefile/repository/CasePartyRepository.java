package com.courtai.casefile.repository;

import com.courtai.casefile.entity.CaseParty;
import com.courtai.common.enums.PartyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Repository for CaseParty — parties involved in a judicial case. */
@Repository
public interface CasePartyRepository extends JpaRepository<CaseParty, Long> {

    List<CaseParty> findByCaseFileIdAndIsDeletedFalse(Long caseFileId);

    List<CaseParty> findByCaseFileUuidAndIsDeletedFalse(String caseFileUuid);

    List<CaseParty> findByCaseFileIdAndPartyTypeAndIsDeletedFalse(Long caseFileId, PartyType partyType);

    Optional<CaseParty> findByUuidAndIsDeletedFalse(String uuid);
}

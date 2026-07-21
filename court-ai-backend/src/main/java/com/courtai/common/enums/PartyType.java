package com.courtai.common.enums;

/**
 * Role of a party in a judicial case.
 */
public enum PartyType {

    /** Party initiating the case. */
    PETITIONER,

    /** Party opposing the petition. */
    RESPONDENT,

    /** Third party allowed to participate by the court. */
    INTERVENOR,

    /** Individual called to give testimony. */
    WITNESS,

    /** Friend of the court — neutral expert assisting the bench. */
    AMICUS_CURIAE,
    
    // Extended Party Types for the Wizard
    CO_PETITIONER,
    CO_PLAINTIFF,
    CO_RESPONDENT,
    CO_DEFENDANT,
    CO_COMPLAINANT,
    CO_INFORMANT,
    CO_ACCUSED,
    PROFORMA_RESPONDENT,
    PROFORMA_DEFENDANT,
    CAVEATOR,
    VICTIM,
    POA_HOLDER,
    LEGAL_HEIR,
    LEGAL_REPRESENTATIVE,
    GUARDIAN_AD_LITEM,
    OFFICIAL_LIQUIDATOR,
    OFFICIAL_RECEIVER,
    OTHER
}

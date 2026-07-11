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
    AMICUS_CURIAE
}

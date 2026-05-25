CREATE OR REPLACE PROCEDURE addElectricLocomotiveModel (
    p_rsm_id          IN RollingStockModel.id%TYPE,
    p_rsm_model       IN RollingStockModel.model%TYPE,
    p_rsm_numBogies   IN RollingStockModel.numBogies%TYPE,
    p_rsm_maxSpeed    IN RollingStockModel.maxSpeed%TYPE,
    p_rsm_bogies      IN RollingStockModel.bogies%TYPE,
    p_rsmRSD_id       IN RollingStockModel.RollingStockDimensionId%TYPE,
    p_rsmMan_id       IN RollingStockModel.manufacturerID%TYPE,
    p_rsm_multipleGauges        IN RollingStockModel.multipleGauges%TYPE,
    p_locomotiveModel_id        IN LocomotiveModel.locomotiveModelID%TYPE,
    p_locomotiveModel_power     IN LocomotiveModel.power%TYPE,
    p_locomotiveModel_os        IN LocomotiveModel.operationalSpeed%TYPE,
    p_locomotiveModel_traction  IN LocomotiveModel.traction%TYPE,
    p_electricLoc_frequency     IN ElectricLocomotiveType.frequency%TYPE,
    p_electricLoc_voltage       IN ElectricLocomotiveType.voltage%TYPE
)
    IS
    e_null_id               EXCEPTION;
    e_null_modelName        EXCEPTION;
    e_null_numBogies        EXCEPTION;
    e_null_maxSpeed         EXCEPTION;
    e_null_bogies           EXCEPTION;
    e_null_manufacturer     EXCEPTION;
    e_null_dimension        EXCEPTION;
    e_null_locomotive_id    EXCEPTION;
    e_null_power            EXCEPTION;
    e_null_operationalSpeed EXCEPTION;
    e_null_traction         EXCEPTION;
    e_null_frequency        EXCEPTION;
    e_null_voltage          EXCEPTION;
    e_neg_zero_id           EXCEPTION;
    e_neg_zero_loc_id       EXCEPTION;
    v_temp NUMBER;
BEGIN
    IF p_rsm_id IS NULL THEN
        RAISE e_null_id;
    END IF;
    IF p_rsm_model IS NULL THEN
        RAISE e_null_modelName;
    END IF;
    IF p_rsm_numBogies IS NULL THEN
        RAISE e_null_numBogies;
    END IF;
    IF p_rsm_maxSpeed IS NULL THEN
        RAISE e_null_maxSpeed;
    END IF;
    IF p_rsm_bogies IS NULL THEN
        RAISE e_null_bogies;
    END IF;
    IF p_rsmMan_id IS NULL THEN
        RAISE e_null_manufacturer;
    END IF;
    IF p_rsmRSD_id IS NULL THEN
        RAISE e_null_dimension;
    END IF;
    IF p_locomotiveModel_id IS NULL THEN
        RAISE e_null_locomotive_id;
    END IF;
    IF p_locomotiveModel_power IS NULL THEN
        RAISE e_null_power;
    END IF;
    IF p_locomotiveModel_os IS NULL THEN
        RAISE e_null_operationalSpeed;
    END IF;
    IF p_locomotiveModel_traction IS NULL THEN
        RAISE e_null_traction;
    END IF;
    IF p_electricLoc_frequency IS NULL THEN
        RAISE e_null_frequency;
    END IF;
    IF p_electricLoc_voltage IS NULL THEN
        RAISE e_null_voltage;
    END IF;
    IF p_rsm_id <= 0 THEN
        RAISE e_neg_zero_id;
    END IF;
    IF p_locomotiveModel_id <= 0 THEN
        RAISE e_neg_zero_loc_id;
    END IF;

    SELECT 1 INTO v_temp FROM Manufacture WHERE manufacturerID = p_rsmMan_id;
    SELECT 1 INTO v_temp FROM RollingStockDimension WHERE id = p_rsmRSD_id;

    INSERT INTO RollingStockModel (id, model, numBogies, maxSpeed, bogies, RollingStockDimensionId, manufacturerID, multipleGauges)
    VALUES (p_rsm_id, p_rsm_model, p_rsm_numBogies, p_rsm_maxSpeed, p_rsm_bogies, p_rsmRSD_id, p_rsmMan_id, p_rsm_multipleGauges);

    INSERT INTO LocomotiveModel (locomotiveModelID, power, operationalSpeed, traction, RollingStockModelId)
    VALUES (p_locomotiveModel_id, p_locomotiveModel_power, p_locomotiveModel_os, p_locomotiveModel_traction, p_rsm_id);

    INSERT INTO ElectricLocomotiveType (locomotiveModelID, frequency, voltage)
    VALUES (p_locomotiveModel_id, p_electricLoc_frequency, p_electricLoc_voltage);
EXCEPTION
    WHEN e_null_id THEN
        RAISE_APPLICATION_ERROR(-20001, 'Rolling Stock Model ID cannot be null.');
    WHEN e_null_modelName THEN
        RAISE_APPLICATION_ERROR(-20002, 'Model name cannot be null.');
    WHEN e_null_numBogies THEN
        RAISE_APPLICATION_ERROR(-20003, 'Number of bogies cannot be null.');
    WHEN e_null_maxSpeed THEN
        RAISE_APPLICATION_ERROR(-20004, 'Max speed cannot be null.');
    WHEN e_null_bogies THEN
        RAISE_APPLICATION_ERROR(-20005, 'Bogies information cannot be null.');
    WHEN e_null_manufacturer THEN
        RAISE_APPLICATION_ERROR(-20006, 'Manufacturer ID cannot be null.');
    WHEN e_null_dimension THEN
        RAISE_APPLICATION_ERROR(-20007, 'RollingStockDimension ID cannot be null.');
    WHEN e_null_locomotive_id THEN
        RAISE_APPLICATION_ERROR(-20008, 'LocomotiveModel ID cannot be null.');
    WHEN e_null_power THEN
        RAISE_APPLICATION_ERROR(-20009, 'Power cannot be null.');
    WHEN e_null_operationalSpeed THEN
        RAISE_APPLICATION_ERROR(-20010, 'Operational speed cannot be null.');
    WHEN e_null_traction THEN
        RAISE_APPLICATION_ERROR(-20011, 'Traction cannot be null.');
    WHEN e_null_frequency THEN
        RAISE_APPLICATION_ERROR(-20012, 'Frequency cannot be null.');
    WHEN e_null_voltage THEN
        RAISE_APPLICATION_ERROR(-20013, 'Voltage cannot be null.');
    WHEN e_neg_zero_id THEN
        RAISE_APPLICATION_ERROR(-20014, 'Rolling Stock Model ID cannot be 0 or negative.');
    WHEN e_neg_zero_loc_id THEN
        RAISE_APPLICATION_ERROR(-20015, 'LocomotiveModel ID cannot be 0 or negative.');
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20016, 'Manufacturer or RollingStockDimension does not exist.');
    WHEN DUP_VAL_ON_INDEX THEN
        IF SQLERRM LIKE '%UQ_ROLLINGSTOCKMODEL_MODEL%' THEN
            RAISE_APPLICATION_ERROR(-20017, 'Model name "' || p_rsm_model || '" already exists.');
        ELSE
            RAISE_APPLICATION_ERROR(-20018, 'ID already exists in one of the tables.');
        END IF;
    WHEN OTHERS THEN
        RAISE;
END;
/

DECLARE
    p_rsmId1 RollingStockModel.id%TYPE := 200;
    p_rsmId2 RollingStockModel.id%TYPE := 201;
    p_rsmId3 RollingStockModel.id%TYPE := 202;

    p_rsmModel1 RollingStockModel.model%TYPE := 'New Model 1';
    p_rsmModel2 RollingStockModel.model%TYPE := 'New Model 2';
    p_rsmModel3 RollingStockModel.model%TYPE := 'New Model 3';

    p_rsmNumBogies1 RollingStockModel.numBogies%TYPE := NULL;
    p_rsmNumBogies2 RollingStockModel.numBogies%TYPE := NULL;
    p_rsmNumBogies3 RollingStockModel.numBogies%TYPE := NULL;

    p_rsmMaxSpeed1 RollingStockModel.maxSpeed%TYPE := 300;
    p_rsmMaxSpeed2 RollingStockModel.maxSpeed%TYPE := 300;
    p_rsmMaxSpeed3 RollingStockModel.maxSpeed%TYPE := 300;

    p_rsmBogies1 RollingStockModel.bogies%TYPE := 'Simple';
    p_rsmBogies2 RollingStockModel.bogies%TYPE := 'Simple';
    p_rsmBogies3 RollingStockModel.bogies%TYPE := 'Simple';

    p_rsmRSDId1 RollingStockModel.RollingStockDimensionId%TYPE := 2;
    p_rsmRSDId2 RollingStockModel.RollingStockDimensionId%TYPE := 3;
    p_rsmRSDId3 RollingStockModel.RollingStockDimensionId%TYPE := 4;

    p_rsmManId1 RollingStockModel.manufacturerID%TYPE := 3;
    p_rsmManId2 RollingStockModel.manufacturerID%TYPE := 4;
    p_rsmManId3 RollingStockModel.manufacturerID%TYPE := 5;

    p_rsmMultipleGauges1 RollingStockModel.multipleGauges%TYPE := 'N';
    p_rsmMultipleGauges2 RollingStockModel.multipleGauges%TYPE := 'N';
    p_rsmMultipleGauges3 RollingStockModel.multipleGauges%TYPE := 'N';

    p_locomotiveModelId1 LocomotiveModel.locomotiveModelID%TYPE := 200;
    p_locomotiveModelId2 LocomotiveModel.locomotiveModelID%TYPE := 201;
    p_locomotiveModelId3 LocomotiveModel.locomotiveModelID%TYPE := 202;

    p_locomotiveModelPower1 LocomotiveModel.power%TYPE := NULL;
    p_locomotiveModelPower2 LocomotiveModel.power%TYPE := NULL;
    p_locomotiveModelPower3 LocomotiveModel.power%TYPE := NULL;

    p_locomotiveModelOS1 LocomotiveModel.operationalSpeed%TYPE := NULL;
    p_locomotiveModelOS2 LocomotiveModel.operationalSpeed%TYPE := NULL;
    p_locomotiveModelOS3 LocomotiveModel.operationalSpeed%TYPE := NULL;

    p_locomotiveModelTraction1 LocomotiveModel.traction%TYPE := 200;
    p_locomotiveModelTraction2 LocomotiveModel.traction%TYPE := 250;
    p_locomotiveModelTraction3 LocomotiveModel.traction%TYPE := 300;

    p_electricLocFrequency1 ElectricLocomotiveType.frequency%TYPE := 30;
    p_electricLocFrequency2 ElectricLocomotiveType.frequency%TYPE := 35;
    p_electricLocFrequency3 ElectricLocomotiveType.frequency%TYPE := 40;

    p_electricLocVoltage1 ElectricLocomotiveType.voltage%TYPE := 10000;
    p_electricLocVoltage2 ElectricLocomotiveType.voltage%TYPE := 20000;
    p_electricLocVoltage3 ElectricLocomotiveType.voltage%TYPE := 30000;
BEGIN
    addElectricLocomotiveModel(p_rsm_id => p_rsmId1,
                               p_rsm_model => p_rsmModel1,
                               p_rsm_numBogies => p_rsmNumBogies1,
                               p_rsm_maxSpeed => p_rsmMaxSpeed1,
                               p_rsm_bogies => p_rsmBogies1,
                               p_rsmRSD_id => p_rsmRSDId1,
                               p_rsmMan_id => p_rsmManId1,
                               p_rsm_multipleGauges => p_rsmMultipleGauges1,
                               p_locomotiveModel_id => p_locomotiveModelId1,
                               p_locomotiveModel_power => p_locomotiveModelPower1,
                               p_locomotiveModel_os => p_locomotiveModelOS1,
                               p_locomotiveModel_traction => p_locomotiveModelTraction1,
                               p_electricLoc_frequency => p_electricLocFrequency1,
                               p_electricLoc_voltage => p_electricLocVoltage1);

    addElectricLocomotiveModel(p_rsm_id => p_rsmId2,
                               p_rsm_model => p_rsmModel2,
                               p_rsm_numBogies => p_rsmNumBogies2,
                               p_rsm_maxSpeed => p_rsmMaxSpeed2,
                               p_rsm_bogies => p_rsmBogies2,
                               p_rsmRSD_id => p_rsmRSDId2,
                               p_rsmMan_id => p_rsmManId2,
                               p_rsm_multipleGauges => p_rsmMultipleGauges2,
                               p_locomotiveModel_id => p_locomotiveModelId2,
                               p_locomotiveModel_power => p_locomotiveModelPower2,
                               p_locomotiveModel_os => p_locomotiveModelOS2,
                               p_locomotiveModel_traction => p_locomotiveModelTraction2,
                               p_electricLoc_frequency => p_electricLocFrequency2,
                               p_electricLoc_voltage => p_electricLocVoltage2);

    addElectricLocomotiveModel(p_rsm_id => p_rsmId3,
                               p_rsm_model => p_rsmModel3,
                               p_rsm_numBogies => p_rsmNumBogies3,
                               p_rsm_maxSpeed => p_rsmMaxSpeed3,
                               p_rsm_bogies => p_rsmBogies3,
                               p_rsmRSD_id => p_rsmRSDId3,
                               p_rsmMan_id => p_rsmManId3,
                               p_rsm_multipleGauges => p_rsmMultipleGauges3,
                               p_locomotiveModel_id => p_locomotiveModelId3,
                               p_locomotiveModel_power => p_locomotiveModelPower3,
                               p_locomotiveModel_os => p_locomotiveModelOS3,
                               p_locomotiveModel_traction => p_locomotiveModelTraction3,
                               p_electricLoc_frequency => p_electricLocFrequency3,
                               p_electricLoc_voltage => p_electricLocVoltage3);
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/

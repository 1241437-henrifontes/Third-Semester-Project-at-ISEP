DECLARE
    p_rsmId1 RollingStockModel.id%TYPE := 100;
    p_rsmId2 RollingStockModel.id%TYPE := 100;

    p_rsmModel1 RollingStockModel.model%TYPE := 'Model 1';
    p_rsmModel2 RollingStockModel.model%TYPE := 'Model 2';

    p_rsmNumBogies1 RollingStockModel.numBogies%TYPE := 2;
    p_rsmNumBogies2 RollingStockModel.numBogies%TYPE := 2;

    p_rsmMaxSpeed1 RollingStockModel.maxSpeed%TYPE := 220;
    p_rsmMaxSpeed2 RollingStockModel.maxSpeed%TYPE := 320;

    p_rsmBogies1 RollingStockModel.bogies%TYPE := 'Simple';
    p_rsmBogies2 RollingStockModel.bogies%TYPE := 'Simple';

    p_rsmRSDId1 RollingStockModel.RollingStockDimensionId%TYPE := 9;
    p_rsmRSDId2 RollingStockModel.RollingStockDimensionId%TYPE := 10;

    p_rsmManId1 RollingStockModel.manufacturerID%TYPE := 4;
    p_rsmManId2 RollingStockModel.manufacturerID%TYPE := 5;

    p_rsmMultipleGauges1 RollingStockModel.multipleGauges%TYPE := 'Y';
    p_rsmMultipleGauges2 RollingStockModel.multipleGauges%TYPE := 'Y';

    p_locomotiveModelId1 LocomotiveModel.locomotiveModelID%TYPE := 80;
    p_locomotiveModelId2 LocomotiveModel.locomotiveModelID%TYPE := 90;

    p_locomotiveModelPower1 LocomotiveModel.power%TYPE := 7000;
    p_locomotiveModelPower2 LocomotiveModel.power%TYPE := 7500;

    p_locomotiveModelOS1 LocomotiveModel.operationalSpeed%TYPE := 100;
    p_locomotiveModelOS2 LocomotiveModel.operationalSpeed%TYPE := 120;

    p_locomotiveModelTraction1 LocomotiveModel.traction%TYPE := 400;
    p_locomotiveModelTraction2 LocomotiveModel.traction%TYPE := 450;

    p_electricLocFrequency1 ElectricLocomotiveType.frequency%TYPE := 50;
    p_electricLocFrequency2 ElectricLocomotiveType.frequency%TYPE := 60;

    p_electricLocVoltage1 ElectricLocomotiveType.voltage%TYPE := 30000;
    p_electricLocVoltage2 ElectricLocomotiveType.voltage%TYPE := 40000;
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
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/

DROP TABLE DieselLocomotiveType CASCADE CONSTRAINTS;
DROP TABLE ElectricLocomotiveType CASCADE CONSTRAINTS;
DROP TABLE Facility CASCADE CONSTRAINTS;
DROP TABLE Gauge CASCADE CONSTRAINTS;
DROP TABLE Line CASCADE CONSTRAINTS;
DROP TABLE LineSegment CASCADE CONSTRAINTS;
DROP TABLE Locomotive CASCADE CONSTRAINTS;
DROP TABLE LocomotiveModel CASCADE CONSTRAINTS;
DROP TABLE Manufacture CASCADE CONSTRAINTS;
DROP TABLE Operator CASCADE CONSTRAINTS;
DROP TABLE Owner CASCADE CONSTRAINTS;
DROP TABLE Company CASCADE CONSTRAINTS;
DROP TABLE RollingStock CASCADE CONSTRAINTS;
DROP TABLE RollingStockDimension CASCADE CONSTRAINTS;
DROP TABLE RollingStockModel CASCADE CONSTRAINTS;
DROP TABLE Wagon CASCADE CONSTRAINTS;
DROP TABLE WagonModel CASCADE CONSTRAINTS;
DROP TABLE Siding CASCADE CONSTRAINTS;
DROP TABLE Freight_Wagon CASCADE CONSTRAINTS;
DROP TABLE Freight CASCADE CONSTRAINTS;
DROP TABLE Train CASCADE CONSTRAINTS;
DROP TABLE Train_Locomotive CASCADE CONSTRAINTS;
DROP TABLE Path CASCADE CONSTRAINTS;
DROP TABLE WagonType CASCADE CONSTRAINTS;
DROP TABLE Good CASCADE CONSTRAINTS;
DROP TABLE Wagon_Good CASCADE CONSTRAINTS;
DROP TABLE Good_WagonType CASCADE CONSTRAINTS;
DROP TABLE RollingStockModel_Gauge CASCADE CONSTRAINTS;
DROP TABLE Station CASCADE CONSTRAINTS;
DROP TABLE Terminal CASCADE CONSTRAINTS;
DROP TABLE Building CASCADE CONSTRAINTS;
DROP TABLE Facility_Building CASCADE CONSTRAINTS;

CREATE TABLE Company (
    vatNumber varchar2(11)
    CONSTRAINT pk_Company_vatNumber PRIMARY KEY,
    name      varchar2(50)
    CONSTRAINT uq_Company_name UNIQUE,
    shortName varchar2(25)
    CONSTRAINT nn_Company_shortName NOT NULL
);

CREATE TABLE RollingStockModel (
    id        number(10)
    CONSTRAINT pk_RollingStockModel_id PRIMARY KEY,
    model     varchar2(30)
    CONSTRAINT uq_RollingStockModel_model UNIQUE,
    numBogies number(10)
    CONSTRAINT nn_RollingStockModel_numBogies NOT NULL,
    maxSpeed  number(10)
    CONSTRAINT nn_RollingStockModel_maxSpeed NOT NULL,
    bogies    varchar2(10)
    CONSTRAINT nn_RollingStockModel_bogies NOT NULL,
    RollingStockDimensionId   number(10)
    CONSTRAINT nn_RollingStockModel_RollingStockDimensionId NOT NULL,
    manufacturerID            number(10)
    CONSTRAINT nn_RollingStockModel_manufacturerID NOT NULL,
    multipleGauges          char(1)
    CONSTRAINT nn_RollingStockModel_multipleGauges NOT NULL
);

CREATE TABLE Siding (
    id          number(10)
    CONSTRAINT pk_Siding_id PRIMARY KEY,
    position    number(10)
    CONSTRAINT nn_Siding_position NOT NULL,
    length      number(10)
    CONSTRAINT nn_Siding_length NOT NULL
);

CREATE TABLE Freight (
    id           number(10)
    CONSTRAINT pk_Freight_id PRIMARY KEY,
    TrainId      number(10)
    CONSTRAINT nn_Freight_TrainId NOT NULL,
    origin       number(10)
    CONSTRAINT nn_Freight_origin NOT NULL,
    destination  number(10)
    CONSTRAINT nn_Freight_destination NOT NULL
);

CREATE TABLE Train (
    id        number(10)
    CONSTRAINT pk_Train_id PRIMARY KEY,
    maxLength  number(10)
    CONSTRAINT nn_Train_maxLength NOT NULL,
    OperatorManagingVatNumber varchar2(11)
    CONSTRAINT nn_Train_OperatorManagingVatNumber NOT NULL
);

CREATE TABLE Path (
    "order"       varchar2(100),
    trainId       number(10),
    CONSTRAINT pk_Path_order_trainId PRIMARY KEY ("order", trainId),
    pathId        number(10)
    CONSTRAINT uq_Path_pathId UNIQUE,
    departureTime timestamp
    CONSTRAINT nn_Path_departureTime NOT NULL,
    facilityId    number(10)
    CONSTRAINT nn_Path_facilityId NOT NULL
);

CREATE TABLE LocomotiveModel (
    locomotiveModelID       number(10)
    CONSTRAINT pk_LocomotiveModel_locomotiveModelID PRIMARY KEY,
    power                   number(8, 2)
    CONSTRAINT nn_LocomotiveModel_power NOT NULL,
    operationalSpeed        number(5)
    CONSTRAINT nn_LocomotiveModel_operationalSpeed NOT NULL,
    traction                number(6, 2)
    CONSTRAINT nn_LocomotiveModel_traction NOT NULL,
    RollingStockModelId     number(10)
    CONSTRAINT nn_LocomotiveModel_RollingStockModelId NOT NULL
);

CREATE TABLE Manufacture (
    manufacturerID number(10)
    CONSTRAINT pk_Manufacture_manufactureID PRIMARY KEY,
    name           varchar2(30)
    CONSTRAINT uq_Manufacture_name UNIQUE
);

CREATE TABLE Gauge (
    gaugeID    number(10)
    CONSTRAINT pk_Gauge_gaugeID PRIMARY KEY,
    gaugeWidth number(10)
    CONSTRAINT uq_Gauge_gaugeWidth UNIQUE
);

CREATE TABLE ElectricLocomotiveType (
    locomotiveModelID number(10)
    CONSTRAINT pk_ElectricLocomotiveType_locomotiveModelID PRIMARY KEY,
    frequency         number(10)
    CONSTRAINT nn_ElectricLocomotiveType_frequency NOT NULL,
    voltage           number(10)
    CONSTRAINT nn_ElectricLocomotiveType_voltage NOT NULL
);

CREATE TABLE DieselLocomotiveType (
    locomotiveModelID number(10)
    CONSTRAINT pk_DieselLocomotiveType_locomotiveModelID PRIMARY KEY,
    fuelCapacity      number(10)
    CONSTRAINT nn_DieselLocomotiveType_fuelCapacity NOT NULL
);

CREATE TABLE Operator (
    ManagingVatNumber varchar2(11)
    CONSTRAINT pk_Operator_ManagingVatNumber PRIMARY KEY
);

CREATE TABLE Line (
    lineId                 number(10)
    CONSTRAINT pk_Line_lineId PRIMARY KEY,
    name                   varchar2(50)
    CONSTRAINT uq_Line_name UNIQUE,
    startFacilityId        number(10)
    CONSTRAINT nn_Line_startFacilityId NOT NULL,
    endFacilityId          number(10)
    CONSTRAINT nn_Line_endFacilityId NOT NULL,
    gaugeID                number(10)
    CONSTRAINT nn_Line_gaugeID NOT NULL,
    "start"                varchar2(30)
    CONSTRAINT nn_Line_start NOT NULL,
    "end"                  varchar2(30)
    CONSTRAINT nn_Line_end NOT NULL,
    OwnerManagingVatNumber varchar2(11)
    CONSTRAINT nn_Line_OwnerManagingVatNumber NOT NULL
);

CREATE TABLE Owner (
    ManagingVatNumber varchar2(11)
    CONSTRAINT pk_Owner_ManagingVatNumber PRIMARY KEY
);

CREATE TABLE LineSegment (
    lineId         number(10),
    "order"        number(10),
    CONSTRAINT pk_LineSegment_lineId_order PRIMARY KEY (lineId, "order"),
    isElectrified  char(1)
    CONSTRAINT nn_LineSegment_isElectrified NOT NULL,
    maxWeight      number(10)
    CONSTRAINT nn_LineSegment_maxWeight NOT NULL,
    length         number(10)
    CONSTRAINT nn_LineSegment_length NOT NULL,
    numberOfTracks number(10)
    CONSTRAINT nn_LineSegment_numberOfTracks NOT NULL,
    sidingId       number(10)
    CONSTRAINT LineSegment_SidingId NULL
);

CREATE TABLE Facility (
    facilityId     number(10)
    CONSTRAINT pk_Facility_facilityId PRIMARY KEY,
    name           varchar2(30)
    CONSTRAINT uq_Facility_name UNIQUE
);

CREATE TABLE RollingStockDimension (
    id     number(10)
    CONSTRAINT pk_RollingStockDimension_id PRIMARY KEY,
    length number(10)
    CONSTRAINT nn_RollingStockDimension_length NOT NULL,
    width  number(10)
    CONSTRAINT nn_RollingStockDimension_width NOT NULL,
    height number(10)
    CONSTRAINT nn_RollingStockDimension_height NOT NULL,
    weight number(10)
    CONSTRAINT nn_RollingStockDimension_weight NOT NULL
);

CREATE TABLE WagonModel (
    id                      number(10)
    CONSTRAINT pk_WagonModel_id PRIMARY KEY,
    payLoad                 number(10)
    CONSTRAINT nn_WagonModel_payLoad NOT NULL,
    volume                  number(10)
    CONSTRAINT nn_WagonModel_volume NOT NULL,
    RollingStockModelId     number(10)
    CONSTRAINT nn_WagonModel_RollingStockModelId NOT NULL,
    WagonTypeId             number(10)
    CONSTRAINT nn_WagonModel_WagonTypeId NOT NULL
);

CREATE TABLE RollingStock (
    id                        number(10)
    CONSTRAINT pk_RollingStock_id PRIMARY KEY,
    startDate                 date
    CONSTRAINT nn_RollingStock_startDate NOT NULL,
    RollingStockModelId       number(10)
    CONSTRAINT nn_RollingStock_RollingStockModelId NOT NULL,
    OperatorManagingVatNumber varchar2(11)
    CONSTRAINT nn_RollingStock_OperatorManagingVatNumber NOT NULL
);

CREATE TABLE Locomotive (
    id                  number(10)
    CONSTRAINT pk_Locomotive_id PRIMARY KEY,
    locomotiveModelID   number(10)
    CONSTRAINT nn_Locomotive_locomotiveModelID NOT NULL,
    name                varchar2(20)
    CONSTRAINT nn_Locomotive_name NOT NULL,
    RollingStockId      number(10)
    CONSTRAINT nn_Locomotive_RollingStockId NOT NULL
);

CREATE TABLE Wagon (
    wagonId             varchar2(9)
    CONSTRAINT pk_Wagon_wagonId PRIMARY KEY,
    RollingStockId      number(10)
    CONSTRAINT nn_Wagon_RollingStockId NOT NULL
);

CREATE TABLE Freight_Wagon (
    FreightId        number(10),
    wagonId          varchar2(9),
    CONSTRAINT pk_Freight_Wagon_FreightId_wagonId PRIMARY KEY (FreightId, wagonId)
);

CREATE TABLE Train_Locomotive (
    TrainId          number(10),
    LocomotiveId     number(10),
    CONSTRAINT pk_Train_Locomotive_TrainId_LocomotiveId PRIMARY KEY (TrainId, LocomotiveId)
);

CREATE TABLE WagonType (
    typeId    number(10)
    CONSTRAINT pk_WagonType_typeId PRIMARY KEY,
    name      varchar2(35)
    CONSTRAINT nn_WagonType_name NOT NULL
);

CREATE TABLE Good (
    id        number(10)
    CONSTRAINT pk_Good_id PRIMARY KEY,
    name      varchar2(30)
    CONSTRAINT nn_Good_name NOT NULL
);

CREATE TABLE Wagon_Good (
    WagonId   varchar2(9),
    GoodId    number(10),

    CONSTRAINT pk_Wagon_Good_WagonId_GoodId PRIMARY KEY (WagonId, GoodId)
);

CREATE TABLE Good_WagonType (
    GoodId        number(10),
    WagonTypeId   number(10),

    CONSTRAINT pk_Good_WagonType_GoodId_WagonTypeId PRIMARY KEY (GoodId, WagonTypeId)
);

CREATE TABLE RollingStockModel_Gauge (
    gaugeID       number(10),
    modelID       number(10),

    CONSTRAINT pk_RollingStockModel_Gauge_gaugeID_modelID PRIMARY KEY (gaugeID, modelID)
);

CREATE TABLE Station (
    facilityID    number(10)
    CONSTRAINT pk_Station_facilityID PRIMARY KEY
);

CREATE TABLE Terminal (
    facilityID    number(10)
    CONSTRAINT pk_Terminal_facilityID PRIMARY KEY,
    intermodal    char(1)
    CONSTRAINT nn_Terminal_intermodal NOT NULL
);

CREATE TABLE Building (
    id            number(10)
    CONSTRAINT pk_Building_id PRIMARY KEY,
    name          varchar2(30)
    CONSTRAINT nn_Building_name NOT NULL
);

CREATE TABLE Facility_Building (
    facilityID    number(10),
    buildingID    number(10),

    CONSTRAINT pk_Facility_Building_facilityID_buildingID PRIMARY KEY (facilityID, buildingID)
);

ALTER TABLE Owner
    ADD CONSTRAINT FK_Owner_VatNumber
        FOREIGN KEY (ManagingVatNumber)
            REFERENCES Company (vatNumber);

ALTER TABLE Operator
    ADD CONSTRAINT FK_Operator_VatNumber
        FOREIGN KEY (ManagingVatNumber)
            REFERENCES Company (vatNumber);

ALTER TABLE ElectricLocomotiveType
    ADD CONSTRAINT FK_ElectricLoc_ModelID
        FOREIGN KEY (locomotiveModelID)
            REFERENCES LocomotiveModel (locomotiveModelID);

ALTER TABLE DieselLocomotiveType
    ADD CONSTRAINT FK_DieselLoco_ModelID
        FOREIGN KEY (locomotiveModelID)
            REFERENCES LocomotiveModel (locomotiveModelID);

ALTER TABLE Locomotive
    ADD CONSTRAINT FK_Locomotive_ModelID
        FOREIGN KEY (locomotiveModelID)
            REFERENCES LocomotiveModel (locomotiveModelID);

ALTER TABLE Locomotive
    ADD CONSTRAINT FK_Locomotive_RSID
            FOREIGN KEY (RollingStockId)
            REFERENCES RollingStock (id);

ALTER TABLE Line
    ADD CONSTRAINT FK_Line_VatNumber
        FOREIGN KEY (OwnerManagingVatNumber)
            REFERENCES Owner (ManagingVatNumber);

ALTER TABLE LineSegment
    ADD CONSTRAINT FK_LineSegment_LineID
        FOREIGN KEY (lineId)
            REFERENCES Line (lineId);

ALTER TABLE LineSegment
    ADD CONSTRAINT FK_LineSegment_SidingID
        FOREIGN KEY (SidingId)
            REFERENCES Siding (id);

ALTER TABLE Line
    ADD CONSTRAINT FK_Line_GaugeID
        FOREIGN KEY (gaugeID)
            REFERENCES Gauge (gaugeID);

ALTER TABLE LocomotiveModel
    ADD CONSTRAINT FK_LocomotiveModel_RSModelID
        FOREIGN KEY (RollingStockModelId)
            REFERENCES RollingStockModel (id);

ALTER TABLE WagonModel
    ADD CONSTRAINT FK_WagonModel_RSMID
        FOREIGN KEY (RollingStockModelId)
            REFERENCES RollingStockModel (id);

ALTER TABLE WagonModel
    ADD CONSTRAINT FK_WagonModel_WagonTypeID
        FOREIGN KEY (WagonTypeId)
            REFERENCES WagonType (typeId);

ALTER TABLE RollingStock
    ADD CONSTRAINT FK_RollingStock_VatNumber
        FOREIGN KEY (OperatorManagingVatNumber)
            REFERENCES Operator (ManagingVatNumber);

ALTER TABLE RollingStock
    ADD CONSTRAINT FK_RollingStock_ModelID
        FOREIGN KEY (RollingStockModelId)
            REFERENCES RollingStockModel (id);

ALTER TABLE Wagon
    ADD CONSTRAINT FK_Wagon_RSID
        FOREIGN KEY (RollingStockId)
            REFERENCES RollingStock (id);

ALTER TABLE Freight_Wagon
    ADD CONSTRAINT FK_Freight_Wagon_FreightID
        FOREIGN KEY (FreightId)
            REFERENCES Freight (id);

ALTER TABLE Freight_Wagon
    ADD CONSTRAINT FK_Freight_Wagon_WagonID
        FOREIGN KEY (wagonId)
            REFERENCES Wagon (wagonId);

ALTER TABLE Train_Locomotive
    ADD CONSTRAINT FK_Train_Locomotive_TrainID
        FOREIGN KEY (TrainId)
            REFERENCES Train (id);

ALTER TABLE Train_Locomotive
    ADD CONSTRAINT FK_Train_Locomotive_LocomotiveID
        FOREIGN KEY (LocomotiveId)
            REFERENCES Locomotive (Id);

ALTER TABLE Train
    ADD CONSTRAINT FK_Train_VatNumber
        FOREIGN KEY (OperatorManagingVatNumber)
            REFERENCES Operator (ManagingVatNumber);

ALTER TABLE Path
    ADD CONSTRAINT FK_Path_TrainID
        FOREIGN KEY (trainId)
            REFERENCES Train (id);

ALTER TABLE Freight
    ADD CONSTRAINT FK_Freight_TrainID
        FOREIGN KEY (TrainId)
            REFERENCES Train (id);

ALTER TABLE Freight
    ADD CONSTRAINT FK_Freight_OriginID
        FOREIGN KEY (origin)
            REFERENCES Facility (facilityId);

ALTER TABLE Freight
    ADD CONSTRAINT FK_Freight_DestinationID
        FOREIGN KEY (destination)
            REFERENCES Facility (facilityId);

ALTER TABLE RollingStockModel
    ADD CONSTRAINT FK_RollingStockModel_RSDID
        FOREIGN KEY (RollingStockDimensionId)
            REFERENCES RollingStockDimension (id);

ALTER TABLE Good_WagonType
    ADD CONSTRAINT FK_Good_WagonType_GoodID
        FOREIGN KEY (GoodId)
            REFERENCES Good (id);

ALTER TABLE Good_WagonType
    ADD CONSTRAINT FK_Good_WagonType_TypeID
        FOREIGN KEY (WagonTypeId)
            REFERENCES WagonType (typeId);

ALTER TABLE Wagon_Good
    ADD CONSTRAINT FK_Wagon_Good_WagonID
        FOREIGN KEY (WagonId)
            REFERENCES Wagon (wagonId);

ALTER TABLE Wagon_Good
    ADD CONSTRAINT FK_Wagon_Good_GoodID
        FOREIGN KEY (GoodId)
            REFERENCES Good (id);

ALTER TABLE RollingStockModel_Gauge
    ADD CONSTRAINT FK_RollingStockModel_Gauge_gaugeID
        FOREIGN KEY (gaugeID)
            REFERENCES Gauge (gaugeID);

ALTER TABLE RollingStockModel_Gauge
    ADD CONSTRAINT FK_RollingStockModel_Gauge_modelID
        FOREIGN KEY (modelID)
            REFERENCES RollingStockModel (id);

ALTER TABLE RollingStockModel
    ADD CONSTRAINT FK_RollingStockModel_mfID
        FOREIGN KEY (manufacturerID)
            REFERENCES Manufacture (manufacturerID);

ALTER TABLE Path
    ADD CONSTRAINT FK_Path_FacilityID
        FOREIGN KEY (facilityId)
            REFERENCES Facility (facilityId);

ALTER TABLE Station
    ADD CONSTRAINT FK_Station_FacilityID
        FOREIGN KEY (facilityID)
            REFERENCES Facility (facilityID);

ALTER TABLE Terminal
    ADD CONSTRAINT FK_Terminal_FacilityID
        FOREIGN KEY (facilityID)
            REFERENCES Facility (facilityID);

ALTER TABLE Facility_Building
    ADD CONSTRAINT FK_Facility_Building_FacilityID
        FOREIGN KEY (facilityID)
            REFERENCES Facility (facilityID);

ALTER TABLE Facility_Building
    ADD CONSTRAINT FK_Facility_Building_BuildingID
        FOREIGN KEY (buildingID)
            REFERENCES Building (id);


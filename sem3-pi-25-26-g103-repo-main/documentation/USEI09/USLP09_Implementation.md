# USLP09 - Train Assembly and Route Assignment

## Descrição
Como Traffic Manager, quero poder montar e atribuir um comboio a uma rota.

## Acceptance Criteria
- ✅ O sistema deve permitir a seleção de locomotivas e vagões que farão parte do comboio
- ✅ A lista deve distinguir entre:
  1. Locomotivas e vagões em trânsito (mostrando o destino final da rota)
  2. Locomotivas e vagões atualmente estacionados (apresentando a estação onde estão estacionados, ordenados por distância descendente do ponto de partida da rota)
- ✅ A base de dados deve ser acedida através de funções PL/SQL usando cursores

## Implementação

### 1. Funções PL/SQL (database/lapr/uslp09_functions.sql)

Foram criadas as seguintes funções PL/SQL com cursores:

#### `fn_get_available_locomotives_for_route(p_route_start_station_id)`
- Retorna um cursor com todas as locomotivas disponíveis
- Distingue entre locomotivas PARKED e IN_TRANSIT
- Inclui informações de localização (estação atual ou destino)

#### `fn_get_available_wagons_for_route(p_route_start_station_id)`
- Retorna um cursor com todos os vagões disponíveis
- Distingue entre vagões PARKED e IN_TRANSIT
- Inclui informações de localização

#### `fn_get_all_routes()`
- Retorna um cursor com todas as rotas disponíveis no sistema
- Mostra estação de partida e chegada

#### `sp_assign_train_to_route(...)`
- Procedimento que atribui locomotivas e vagões a um comboio
- Persiste a informação na tabela Train_Locomotive
- Suporta múltiplas locomotivas e vagões (separados por vírgulas)

### 2. Repository Layer (Repositories/LAPR/TrainAssemblyRepository.java)

Repository que implementa o padrão de acesso à base de dados:

- **getAvailableLocomotives(routeStartStationId)**: Chama a função PL/SQL com cursor e converte ResultSet para objetos Locomotive
- **getAvailableWagons(routeStartStationId)**: Chama a função PL/SQL com cursor e converte ResultSet para objetos RailwayWagon
- **getAllRoutes()**: Obtém todas as rotas disponíveis
- **assignTrainToRoute(...)**: Chama o procedimento PL/SQL para persistir a atribuição do comboio

### 3. Controller Layer (Controllers/LAPR/TrainAssemblyController.java)

Controller que implementa a lógica de negócio:

- **getAvailableLocomotivesForRoute()**: 
  - Obtém locomotivas do repository
  - Converte para DTOs
  - Ordena locomotivas estacionadas por distância (descendente)
  
- **getAvailableWagonsForRoute()**:
  - Obtém vagões do repository
  - Converte para DTOs
  - Ordena vagões estacionados por distância (descendente)

- **assembleAndAssignTrain()**:
  - Valida a seleção de locomotivas (pelo menos 1 é obrigatória)
  - Cria o objeto Train
  - Adiciona vagões selecionados
  - Valida compatibilidade de gauge
  - Persiste na base de dados através do repository

### 4. UI Layer (UI/LAPR/TrainAssemblyUI.java)

Interface de utilizador com workflow em 4 passos:

**STEP 1: SELECT A ROUTE**
- Mostra todas as rotas disponíveis
- Permite seleção de uma rota

**STEP 2: SELECT LOCOMOTIVES**
- Lista separada por status:
  - Locomotivas IN TRANSIT (mostra destino final)
  - Locomotivas PARKED (mostra estação + distância, ordenado descendente)
- Permite seleção múltipla (separada por vírgulas)

**STEP 3: SELECT WAGONS (OPTIONAL)**
- Lista separada por status:
  - Vagões IN TRANSIT (mostra destino final)
  - Vagões PARKED (mostra estação + distância, ordenado descendente)
- Permite seleção múltipla ou skip

**STEP 4: CONFIRM AND ASSIGN**
- Mostra resumo da composição
- Solicita Train ID e Operator VAT
- Confirma antes de persistir
- Mostra detalhes do comboio montado (ID, rota, locomotiva, nº vagões, comprimento total, peso total)

### 5. Model Classes

Já existiam mas foram melhorados:

- **Locomotive**: Representa uma locomotiva com especificações técnicas e localização
- **RailwayWagon**: Representa um vagão com capacidades e localização
- **RollingStockLocation**: Rastreia a localização atual (PARKED/IN_TRANSIT) e distância
- **RollingStockStatus**: Enum com estados (PARKED, IN_TRANSIT, MAINTENANCE, AVAILABLE)
- **RollingStockDTO**: DTO para transferência de dados com informações de localização
- **Train**: Representa um comboio com locomotivas e vagões, validando compatibilidade de gauge
- **Route**: Representa uma rota com segmentos e freights

### 6. Menu Integration

O menu Traffic Management (UI/LAPR/TrafficManagement.java) foi atualizado com:
```
1. Route Planner
2. Train Assembly (USLP09)  <- NOVO
3. Scheduler
```

## Como Executar

1. **Criar as funções PL/SQL na base de dados:**
```sql
@database/lapr/uslp09_functions.sql
```

2. **Executar a aplicação:**
- Navegar até Traffic Management
- Selecionar "Train Assembly (USLP09)"
- Seguir o workflow guiado

## Exemplo de Uso

```
STEP 1: Select Route 5421 (Lisboa -> Porto)
STEP 2: Select Locomotives: 5621, 5623
STEP 3: Select Wagons: 356 3 077, 356 3 078, 356 3 079
Enter Train ID: 9001
Enter Operator VAT: 123456789
Confirm: yes

✓ SUCCESS: Train assembled and assigned to route!
- Train ID: 9001
- Locomotives: 2
- Wagons: 3
- Total Length: 85.5 m
- Total Weight: 245,000 kg
```

## Arquitetura

```
┌─────────────────────────────────────────┐
│         TrainAssemblyUI                 │ <- Presentation Layer
│  - Workflow em 4 passos                 │
│  - Formatação de output                 │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│      TrainAssemblyController            │ <- Business Logic Layer
│  - Validação de composição              │
│  - Ordenação por distância              │
│  - Conversão para DTOs                  │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│     TrainAssemblyRepository             │ <- Data Access Layer
│  - Chamadas PL/SQL com CallableStatement│
│  - Gestão de cursores                   │
│  - Conversão ResultSet -> Objects       │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│         Oracle Database                 │ <- Database Layer
│  - Funções PL/SQL com cursores          │
│  - Procedimentos de atribuição          │
│  - Tabelas: Train, Train_Locomotive,    │
│    Locomotive, Wagon, etc.              │
└─────────────────────────────────────────┘
```

## Notas Técnicas

- **Uso de Cursores**: Todas as funções PL/SQL usam `SYS_REFCURSOR` conforme pedido
- **Ordenação**: Locomotivas/vagões estacionados são ordenados por distância do ponto de partida (descendente)
- **Validação**: O sistema valida compatibilidade de gauge antes de adicionar vagões ao comboio
- **Transações**: O procedimento `sp_assign_train_to_route` usa COMMIT/ROLLBACK para garantir consistência

## Ficheiros Criados/Modificados

**Criados:**
- `database/lapr/uslp09_functions.sql`
- `src/main/java/Repositories/LAPR/TrainAssemblyRepository.java`
- `src/main/java/Controllers/LAPR/TrainAssemblyController.java`
- `src/main/java/UI/LAPR/TrainAssemblyUI.java`

**Modificados:**
- `src/main/java/UI/LAPR/TrafficManagement.java` (adicionado menu item)
- `src/main/java/Model/LAPR/Train.java` (adicionados métodos isValid() e getTotalWeight())

## Status
✅ **IMPLEMENTAÇÃO COMPLETA**
- Todas as acceptance criteria foram cumpridas
- Funções PL/SQL com cursores implementadas
- Distinção entre IN_TRANSIT e PARKED
- Ordenação por distância para rolling stock estacionado
- UI completa com workflow guiado
- Integração com o menu Traffic Management


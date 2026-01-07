## Implements the hybrid Caffeine (L1) and Redis (L2) caching architecture
### 1. Configuration & Build
   •	pom.xml: Added spring-boot-starter-cache, caffeine, and spring-boot-starter-data-redis.
   •	src/main/java/com/hugecorp/teamwalk/config/CacheConfig.java:  Defines the CompositeCacheManager and CacheErrorHandler to coordinate both local and distributed layers.
   •	src/main/resources/application.yml: Configure Redis connection details and define the spring.cache.type=composite properties.
### 2. Domain Entities (Serialization)
   All entities were updated to implements Serializable for Redis compatibility:
   •	Employee.java: Added serialization and @ToString.Exclude on the Team relationship.
   •	Team.java: Added serialization and ensured recursive relationships are handled.
   •	StepCounter.java: Added serialization and fixed enum mapping.
   •	State.java (Enum): Added @JsonValue to ensure stable string persistence in Redis.
### 3. Data Transfer Objects (Records)
   Converted to implement Serializable and ensured nested lists are serializable:
   •	TeamDTO.java
   •	EmployeeDTO.java
   •	StepCounterDTO.java
### 4. Service Implementations (Caching Logic)
   Applied caching annotations to the service layer to manage the data flow:
   •	TeamServiceImpl.java: Added @Cacheable for lookups and @CacheEvict for team deletion/updates.
   •	EmployeeServiceImpl.java: Added @Cacheable for employee lookups and @Caching(evict=...) to clear team and leaderboard data when steps are added.
   •	StepCounterServiceImpl.java: Added @Cacheable for the Leaderboard and specific step counter lookups.
### 5. Repositories (Synchronization)
   •	StepCounterRepository.java: Added @CacheEvict to the @Modifying increment query to prevent stale data in the cache after a direct DB update.
### 6. Exception Handling
   •	GlobalExceptionHandler.java: Added handlers for RedisConnectionFailureException to ensure the app falls back to the database gracefully if the L2 cache is down.
### 7. Database Scripts
   •	schema.sql & data.sql: naming conventions and enum strings match the caching serialization format.

### 3. Architecture Summary
   Project follows these 2026 caching standards:
   Layer	Technology	Purpose
   L1 Cache	Caffeine
   Near-zero latency for "hot" data on the local node.
   L2 Cache	Redis	Shared state across all instances to prevent stale data.
   Data Format	DTOs	Prevents Hibernate LazyInitializationException and reduces cache size.
   Resilience	CacheErrorHandler	Ensures the app stays online even if Redis fails (fall back to DB).
   Serialization	Jackson/Serializable	All cached objects (TeamDTO, EmployeeDTO) are serializable for Redis.
   Integration Links
   •	Performance Tuning: Use the Caffeine Configurator to set maximumSize based on your 2026 container memory limits.
   •	Redis Monitoring: Connect to your cache via Redis Insight to verify that DTOs are being stored correctly as JSON or binary blobs.



package bucket.domain.repository

import bucket.domain.entity.Inventory
import org.springframework.data.jpa.repository.JpaRepository

interface InventoryRepository : JpaRepository<Inventory, Long>
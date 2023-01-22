package fr.ymanvieu.trading.common.portofolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import fr.ymanvieu.trading.common.portofolio.entity.PortofolioEntity;

public interface PortofolioRepository extends JpaRepository<PortofolioEntity, Integer>, QuerydslPredicateExecutor<PortofolioEntity> {

	PortofolioEntity findByUserId(Integer userId);
}

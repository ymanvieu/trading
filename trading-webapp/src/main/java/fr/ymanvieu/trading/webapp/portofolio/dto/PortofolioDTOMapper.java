package fr.ymanvieu.trading.webapp.portofolio.dto;

import org.mapstruct.Mapper;

import fr.ymanvieu.trading.common.config.MapstructConfig;
import fr.ymanvieu.trading.common.portofolio.AssetInfo;
import fr.ymanvieu.trading.common.portofolio.OrderInfo;
import fr.ymanvieu.trading.common.portofolio.Portofolio;
import fr.ymanvieu.trading.webapp.symbol.dto.SymbolDTOMapper;

@Mapper(config = MapstructConfig.class, uses = SymbolDTOMapper.class)
public interface PortofolioDTOMapper {

	AssetDTO toAssetDto(AssetInfo p);

	PortofolioDTO toPortofolioDto(Portofolio p);

	OrderInfoDTO toOrderInfoDto(OrderInfo oi);
}

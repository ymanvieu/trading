package fr.ymanvieu.trading.common.symbol.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "favorite_symbol")
@Data
@EqualsAndHashCode(of = {"userId", "fromSymbolCode", "toSymbolCode"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@IdClass(FavoriteSymbolPK.class)
public class FavoriteSymbolEntity implements Serializable {

	private static final long serialVersionUID = 3912360548486565703L;

	@Id
	@Column(name = "user_id")
	private Integer userId;

	@Id
	@Column(name = "from_symbol_code", length = 8)
	private String fromSymbolCode;
	
	@Id
	@Column(name = "to_symbol_code", length = 8)
	private String toSymbolCode;

}

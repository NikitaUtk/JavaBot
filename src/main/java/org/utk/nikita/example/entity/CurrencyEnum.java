package org.utk.nikita.example.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CurrencyEnum {
    USD(431), EUR(451), RUB(456), BYN(0),AMD(1);

    private final int id;
}

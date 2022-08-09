import type {
  PagarModalidadePagamentoEnum,
  PagarTipoFinanciamentoEnum,
  PagarTipoCartaoEnum,
} from '../enums/paygosdk-enum';

export type ConfigurarAutomacaoType = {
  automacao_empresa: string;
  automacao_nome: string;
  automacao_versao: string;

  suporta_troco: boolean;
  suporta_desconto: boolean;

  vias_diferenciadas: boolean;
  via_reduzida: boolean;

  retornar_comprovantes_graficos: boolean;
};

export type ConfigurarPersonalizacaoType = {
  arquivo_fonte?: string;
  arquivo_icone_toolbar?: string;

  cor_fonte?: string;
  cor_fonte_teclado?: string;
  cor_fundo_caixa_edicao?: string;
  cor_fundo_tela?: string;
  cor_fundo_teclado?: string;
  cor_fundo_toolbar?: string;
  cor_texto_caixa_edicao?: string;
  cor_tecla_pressionada_teclado?: string;
  cor_tecla_liberada_teclado?: string;
  cor_separador_menu?: string;
};

export type VenderType = {
  id_transacao_automacao?: string;
  valor_transacao: number;

  modalidade_pagamento?: PagarModalidadePagamentoEnum;
  tipo_cartao?: PagarTipoCartaoEnum;
  tipo_financiamento?: PagarTipoFinanciamentoEnum;

  parcelas?: number;
  adquirente?: string;
};

export type CancelarType = {
  id_transacao_automacao?: string;

  transacao_original_nsu: string;
  transacao_original_codigo_aut: string;
  transacao_original_data_cancelamento: Date;
  transacao_original_valor: number;
};

// export type PagarModalidadePagamentoType =
//     PagarModalidadePagamentoEnum.PAGAMENTO_CARTAO |
//     PagarModalidadePagamentoEnum.PAGAMENTO_DINHEIRO |
//     PagarModalidadePagamentoEnum.PAGAMENTO_CHEQUE |
//     PagarModalidadePagamentoEnum.PAGAMENTO_CARTEIRA_VIRTUAL;

// export type PagarTipoCartaoType =
//     PagarTipoCartaoEnum.CARTAO_DESCONHECIDO |
//     PagarTipoCartaoEnum.CARTAO_CREDITO |
//     PagarTipoCartaoEnum.CARTAO_DEBITO |
//     PagarTipoCartaoEnum.CARTAO_VOUCHER |
//     PagarTipoCartaoEnum.CARTAO_PRIVATELABEL |
//     PagarTipoCartaoEnum.CARTAO_FROTA;

// export type PagarTipoFinanciamentoType =
//     PagarTipoFinanciamentoEnum.FINANCIAMENTO_NAO_DEFINIDO |
//     PagarTipoFinanciamentoEnum.A_VISTA |
//     PagarTipoFinanciamentoEnum.PARCELADO_EMISSOR |
//     PagarTipoFinanciamentoEnum.PARCELADO_ESTABELECIMENTO |
//     PagarTipoFinanciamentoEnum.PRE_DATADO |
//     PagarTipoFinanciamentoEnum.CREDITO_EMISSOR;

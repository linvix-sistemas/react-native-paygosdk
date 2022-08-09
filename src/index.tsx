import { NativeModules, Platform } from 'react-native';

// TYPES
import * as Types from './types/paygosdk-types';

const LINKING_ERROR =
  `The package 'react-native-paygosdk' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n';

const PaygoSdk = NativeModules.PaygoSdk
  ? NativeModules.PaygoSdk
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

/**
 * Configura a automação comercial
 */
const ConfigurarPersonalizacao = async (
  data: Types.ConfigurarPersonalizacaoType
) => {
  try {
    const result = await PaygoSdk.ConfigurarPersonalizacao(
      data.arquivo_fonte,
      data.arquivo_icone_toolbar,

      data.cor_fonte,
      data.cor_fonte_teclado,
      data.cor_fundo_caixa_edicao,
      data.cor_fundo_tela,
      data.cor_fundo_teclado,
      data.cor_fundo_toolbar,
      data.cor_texto_caixa_edicao,
      data.cor_tecla_pressionada_teclado,
      data.cor_tecla_liberada_teclado,
      data.cor_separador_menu
    );

    return JSON.parse(result);
  } catch (error) {
    throw error;
  }
};

/**
 * Configura a automação comercial
 */
const ConfigurarAutomacao = async (data: Types.ConfigurarAutomacaoType) => {
  try {
    const result = await PaygoSdk.ConfigurarAutomacao(
      data.automacao_empresa,
      data.automacao_nome,
      data.automacao_versao,

      data.suporta_troco,
      data.suporta_desconto,

      data.vias_diferenciadas,
      data.via_reduzida,

      data.retornar_comprovantes_graficos
    );

    return JSON.parse(result);
  } catch (error) {
    throw error;
  }
};

/**
 * Faz uma operação administrativa.
 */
const Administrativa = async () => {
  try {
    const result = await PaygoSdk.Administrativo();

    return JSON.parse(result);
  } catch (error) {
    throw error;
  }
};

/**
 * Faz uma transação de venda.
 */
const Vender = async (data: Types.VenderType) => {
  try {
    if (data.valor_transacao <= 0) {
      throw new Error('Valor da transação não pode ser zero');
    }

    // formata o valor da transação para o padrão aceito do paygo
    const valor_transacao = data?.valor_transacao?.toFixed(2)?.replace('.', '');

    const result = await PaygoSdk.Vender(
      data.id_transacao_automacao,
      valor_transacao,
      data.modalidade_pagamento,
      data.tipo_cartao,
      data.tipo_financiamento,
      data.parcelas,
      data.adquirente
    );

    return JSON.parse(result);
  } catch (error) {
    throw error;
  }
};

/**
 * Configura a automação comercial
 */
const Cancelar = async (data: Types.CancelarType) => {
  try {
    const result = await PaygoSdk.Cancelar(
      data.id_transacao_automacao,

      data.transacao_original_nsu,
      data.transacao_original_codigo_aut,
      data.transacao_original_data_cancelamento,
      data.transacao_original_valor
    );

    return JSON.parse(result);
  } catch (error) {
    throw error;
  }
};

const NativeModulePayGoSDK = {
  Types: Types,

  ConfigurarAutomacao,
  ConfigurarPersonalizacao,
  Administrativa,
  Vender,
  Cancelar,
};

export default NativeModulePayGoSDK;

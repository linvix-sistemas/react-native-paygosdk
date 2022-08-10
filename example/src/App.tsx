import React from 'react';
import { StyleSheet, Text, TouchableOpacity, View } from 'react-native';

// NATIVE MODULES
import NativeModulePayGoSDK, {
  PagarTipoCartaoEnum,
  PagarModalidadePagamentoEnum,
  PagarTipoFinanciamentoEnum,
} from '@linvix-sistemas/react-native-paygosdk';

const HomeScreen = () => {
  const goConfigureAutomation = async () => {
    try {
      const result = await NativeModulePayGoSDK.ConfigurarAutomacao({
        automacao_empresa: 'RNPAYGOSDK',
        automacao_nome: 'RNPAYGOSDK',
        automacao_versao: '0.0.1',

        suporta_desconto: false,
        suporta_troco: false,

        vias_diferenciadas: true,
        via_reduzida: false,

        retornar_comprovantes_graficos: true,
      });

      console.log(result);
    } catch (error: Error | any) {
      console.log(error.code);
      console.log(error.message);
      console.log(JSON.stringify(error));
    }
  };

  const onRequestAdministrativa = async () => {
    try {
      await goConfigureAutomation();
      NativeModulePayGoSDK.Administrativa();
    } catch (error: Error | any) {
      console.log(error.code);
      console.log(error.message);
      console.log(JSON.stringify(error));
    }
  };

  const onRequestVenda = async () => {
    try {
      await goConfigureAutomation();

      const result = await NativeModulePayGoSDK.Vender({
        id_transacao_automacao: '',
        valor_transacao: 1.5,
        modalidade_pagamento: PagarModalidadePagamentoEnum.PAGAMENTO_CARTAO,
        tipo_cartao: PagarTipoCartaoEnum.CARTAO_DEBITO,
        tipo_financiamento: PagarTipoFinanciamentoEnum.A_VISTA,
        parcelas: 0,
        adquirente: '',
      });

      console.log(result);
    } catch (error: Error | any) {
      console.log(error.code);
      console.log(error.message);
      console.log(JSON.stringify(error));
    }
  };

  return (
    <View style={Styles.main}>
      <View style={Styles.container}>
        <TouchableOpacity
          style={Styles.button}
          onPress={onRequestAdministrativa}
        >
          <Text style={Styles.buttonText}>Administrativo</Text>
        </TouchableOpacity>

        <TouchableOpacity style={Styles.button} onPress={onRequestVenda}>
          <Text style={Styles.buttonText}>Venda</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
};

const Styles = StyleSheet.create({
  main: {
    display: 'flex',
    flex: 1,
  },

  container: {
    flexGrow: 1,
    justifyContent: 'center',
    alignItems: 'center',
    alignContent: 'center',
  },

  button: {
    marginBottom: 30,
    backgroundColor: 'blue',
    padding: 10,
    borderRadius: 3,
    minWidth: 400,
    justifyContent: 'center',
    alignItems: 'center',
  },

  buttonText: {
    color: '#fff',
  },
});

export default HomeScreen;

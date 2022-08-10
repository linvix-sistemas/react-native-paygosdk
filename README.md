# react-native-paygosdk
Wrapper de comunicação com o PaygoIntegrado para react native.
## Atenção
Este pacote foi desenvolvido para facilitar a integração com o SDK da Paygo para transações TEF junto ao PAYGO INTEGRADO.

Ou seja, este pacote NÃO FUNCIONA sem o Paygo Integrado.

## Paygo
Para conseguir os dados necessários para funcionamento, você precisa de dados fornecidos pela Paygo ou por uma revenda Paygo.


## Instalação

```sh
npm install @linvix-sistemas/react-native-paygosdk
```

```sh
yarn add @linvix-sistemas/react-native-paygosdk
```
## Uso

```js
import NativeModulePayGoSDK, {
  PagarTipoCartaoEnum,
  PagarModalidadePagamentoEnum,
  PagarTipoFinanciamentoEnum,
} from '@linvix-sistemas/react-native-paygosdk';
```

Veja a pasta [example](example/src/App.tsx) para verificar como utilizar.

## Metódos
Lista de métodos expostos para utlização.

### Configurar Automação
```ts
// Função que configura os dados da automação.
// Deve ser chamada sempre antes de todas as operações passando os dados de sua automação.
await NativeModulePayGoSDK.ConfigurarAutomacao(data: ConfigurarAutomacaoType);
```
### Configurar Personalização
```ts
// Operação que customiza a interface do Paygo Integrado.
// Deve ser chamada sempre antes de todas as operações passando os dados para configuração da interface do Paygo Integrado.
await NativeModulePayGoSDK.ConfigurarPersonalizacao(data: ConfigurarPersonalizacaoType);
```
### Administrativa
```ts
// Realiza uma operação administrativa.
await NativeModulePayGoSDK.Administrativa();
```
### Vender
```ts
// Realiza uma operação de venda
await NativeModulePayGoSDK.Vender(data: VenderType);
```
### Cancelar
```ts
// Realiza uma operação de cancelamento de uma transação já aprovada.
await NativeModulePayGoSDK.Cancelar(data: CancelarType);
```

### [Types](src/types/paygosdk-types.ts)
### [Enums](src/enums/paygosdk-enum.ts)
---
## Contribuindo
Fique a vontade para fazer contribuições no projeto, ele é um projeto que a Linvix Sistemas está utilizando em seus projetos e achou conveniente disponibilizar para a comunidade.



## License

MIT


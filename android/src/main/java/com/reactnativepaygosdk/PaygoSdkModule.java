package com.reactnativepaygosdk;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.setis.interfaceautomacao.AplicacaoNaoInstaladaExcecao;
import br.com.setis.interfaceautomacao.Cartoes;
import br.com.setis.interfaceautomacao.Confirmacoes;
import br.com.setis.interfaceautomacao.DadosAutomacao;
import br.com.setis.interfaceautomacao.EntradaTransacao;
import br.com.setis.interfaceautomacao.Financiamentos;
import br.com.setis.interfaceautomacao.Operacoes;
import br.com.setis.interfaceautomacao.Personalizacao;
import br.com.setis.interfaceautomacao.QuedaConexaoTerminalExcecao;
import br.com.setis.interfaceautomacao.SaidaTransacao;
import br.com.setis.interfaceautomacao.TransacaoPendenteDados;
import br.com.setis.interfaceautomacao.Transacoes;

import static br.com.setis.interfaceautomacao.ModalidadesPagamento.*;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@ReactModule(name = PaygoSdkModule.NAME)
public class PaygoSdkModule extends ReactContextBaseJavaModule {
    public static final String NAME = "PaygoSdk";

    private final Context appContext;

    private Confirmacoes mConfirmacao = new Confirmacoes();
    private DadosAutomacao mDadosAutomacao = null;
    private Personalizacao mPersonalizacao;
    private Transacoes mTransacoes = null;
    private SaidaTransacao mSaidaTransacao;
    private EntradaTransacao mEntradaTransacao;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // UUID identificando a transação da automação
    private String identificacaoAutomacao;

    // Dados da Automação
    private String empresaAutomacao = "";
    private String nomeAutomacao = "";
    private String versaoAutomacao = "";

    // Operação
    private int parcelas = 0;
    private String valorOperacao;
    private String adquirente = "";
    private int modalidadePagamento = 0;
    private int tipoCartao = 0;
    private int tipoFinanciamento = 0;

    // Operação Cancelamento
    private String cancelamentoNsu;
    private String cancelamentoDataOperacao;
    private String cancelamentoCodigoAutorizacao;
    private String cancelamentoValorTransacaoOriginal;

    // Configurações
    // private boolean confirmacaoManual = false;
    private boolean suportaViasDiferenciadas = false;
    private boolean suportaViaReduzida = false;
    private boolean suportaTroco = false;
    private boolean suportaDesconto = false;
    private boolean retornaComprovantesGraficos = false; // Interna

    // Interface
    private String fileIconDestino = "";
    private String fileFonteDestino = "";
    private String informaCorFonte = "";
    private String informaCorFonteTeclado = "";
    private String informaCorFundoCaixaEdicao = "";
    private String informaCorFundoTela = "";
    private String informaCorFundoTeclado = "";
    private String informaCorFundoToolbar = "";
    private String informaCorTextoCaixaEdicao = "";
    private String informaCorTeclaPressionadaTeclado = "";
    private String informaCorTeclaLiberadaTeclado = "";
    private String informaCorSeparadorMenu = "";

    private static final String DEBUG_TAG = "[react-native-paygosdk]";

    PaygoSdkModule(ReactApplicationContext context) {
        super(context);
        // pega o contexto da aplicação
        this.appContext = context.getApplicationContext();
    }

    @Override
    @NonNull
    public String getName() {
        return NAME;
    }

    @ReactMethod
    public void ConfigurarAutomacao(
        @NonNull String AutomacaoEmpresa,
        @NonNull String AutomacaoNome,
        @NonNull String AutomacaoVersao,

        @NonNull Boolean SuportaTroco,
        @NonNull Boolean SuportaDesconto,

        @NonNull Boolean ViasDiferenciadas,
        @NonNull Boolean ViaReduzida,

        @NonNull Boolean RetornaComprovantesGraficos,

        final Promise promise
    ) {
        JSONObject json = new JSONObject();
        JSONObject jsond = new JSONObject();
        try {
            this.empresaAutomacao = AutomacaoEmpresa;
            this.nomeAutomacao = AutomacaoNome;
            this.versaoAutomacao = AutomacaoVersao;
            this.suportaTroco = SuportaTroco;
            this.suportaDesconto = SuportaDesconto;
            this.suportaViasDiferenciadas = ViasDiferenciadas;
            this.suportaViaReduzida = ViaReduzida;
            this.retornaComprovantesGraficos = RetornaComprovantesGraficos;

            // json response object
            jsond.put("automacao_empresa", this.empresaAutomacao);
            jsond.put("automacao_nome", this.nomeAutomacao);
            jsond.put("automacao_versao", this.versaoAutomacao);

            jsond.put("suporta_troco", this.suportaTroco);
            jsond.put("suporta_desconto", this.suportaDesconto);

            jsond.put("vias_diferenciadas", this.suportaViasDiferenciadas);
            jsond.put("via_reduzida", this.suportaViaReduzida);

            jsond.put("retorna_comprovantes_graficos", this.retornaComprovantesGraficos);

            json.put("status", true);
            json.put("data", jsond);

            promise.resolve(json.toString());
        } catch (JSONException e) {
            promise.reject(e.getClass().getSimpleName(), e.getMessage(), e.fillInStackTrace());
        }
    }

    @ReactMethod
    public void ConfigurarPersonalizacao(
        @Nullable String ArquivoFonte,
        @Nullable String ArquivoIconeToolbar,

        @Nullable String CorFonte,
        @Nullable String CorFonteTeclado,
        @Nullable String CorFundoCaixaEdicao,
        @Nullable String CorFundoTela,
        @Nullable String CorFundoTeclado,
        @Nullable String CorFundoToolbar,
        @Nullable String CorTextoCaixaEdicao,
        @Nullable String CorTeclaPressionadaTeclado,
        @Nullable String CorTeclaLiberadaTeclado,
        @Nullable String CorSeparadorMenu,

        final Promise promise
    ) {
        JSONObject json = new JSONObject();
        JSONObject jsond = new JSONObject();
        try {
            if (!TextUtils.isEmpty(ArquivoFonte)) {
                File FileFonteToolbar = new File(ArquivoFonte);
                if (FileFonteToolbar.exists()) {
                    this.fileFonteDestino = ArquivoFonte;
                }
            }
            if (!TextUtils.isEmpty(ArquivoIconeToolbar)) {
                File FileIconeToolbar = new File(ArquivoIconeToolbar);
                if (FileIconeToolbar.exists()) {
                    this.fileIconDestino = ArquivoIconeToolbar;
                }
            }
            this.informaCorFonte = CorFonte;
            this.informaCorFonteTeclado = CorFonteTeclado;
            this.informaCorFundoCaixaEdicao = CorFundoCaixaEdicao;
            this.informaCorFundoTela = CorFundoTela;
            this.informaCorFundoTeclado = CorFundoTeclado;
            this.informaCorFundoToolbar = CorFundoToolbar;
            this.informaCorTextoCaixaEdicao = CorTextoCaixaEdicao;
            this.informaCorTeclaPressionadaTeclado = CorTeclaPressionadaTeclado;
            this.informaCorTeclaLiberadaTeclado = CorTeclaLiberadaTeclado;
            this.informaCorSeparadorMenu = CorSeparadorMenu;

            // json response object
            jsond.put("arquivo_fonte", this.fileFonteDestino);
            jsond.put("arquivo_icone_toolbar", this.fileIconDestino);
            jsond.put("cor_fonte", this.informaCorFonte);
            jsond.put("cor_fonte_teclado", this.informaCorFonteTeclado);
            jsond.put("cor_fundo_caixa_edicao", this.informaCorFundoCaixaEdicao);
            jsond.put("cor_fundo_tela", this.informaCorFundoTela);
            jsond.put("cor_fundo_teclado", this.informaCorFundoTeclado);
            jsond.put("cor_fundo_toolbar", this.informaCorFundoToolbar);
            jsond.put("cor_texto_caixa_edicao", this.informaCorTextoCaixaEdicao);
            jsond.put("cor_tecla_pressionada_teclado", this.informaCorTeclaPressionadaTeclado);
            jsond.put("cor_tecla_liberada_teclado", this.informaCorTeclaLiberadaTeclado);
            jsond.put("cor_separador_menu", this.informaCorSeparadorMenu);

            json.put("status", true);
            json.put("data", jsond);

            promise.resolve(json.toString());
        } catch (JSONException e) {
            promise.reject(e.getClass().getSimpleName(), e.getMessage(), e.fillInStackTrace());
        }
    }

    @ReactMethod
    public void Vender(
        // uuid da transação da aplicação
        @NonNull String IDTransacaoAutomacao,
        @NonNull String ValorTransacao,

        @Nullable Integer ModalidadePagamento,
        @Nullable Integer TipoCartao,
        @Nullable Integer TipoFinanciamento,
        @Nullable Integer Parcelas,

        @Nullable String Adquirente,

        final Promise promise
    ) {
        // configura os parâmetros necessários para efetuar a operação
        this.identificacaoAutomacao = IDTransacaoAutomacao;
        this.valorOperacao = ValorTransacao;
        this.modalidadePagamento = ModalidadePagamento;
        this.tipoCartao = TipoCartao;
        this.tipoFinanciamento = TipoFinanciamento;
        this.parcelas = Parcelas;
        this.adquirente = Adquirente;

        // efetua a operação
        EfetuarOperacao(Operacoes.VENDA, promise);
    }

    @ReactMethod
    public void Cancelar(
        // uuid da transação da aplicação
        @NonNull String IDTransacaoAutomacao,
        @NonNull String NSU,
        @NonNull String CodigoAutorizacao,
        @NonNull String DataOperacao,
        @NonNull String ValorTransacaoOriginal,

        final Promise promise
    ) {
        // configura os parâmetros necessários para fazer a operação
        this.identificacaoAutomacao = IDTransacaoAutomacao;
        this.cancelamentoCodigoAutorizacao = CodigoAutorizacao;
        this.cancelamentoNsu = NSU;
        this.cancelamentoDataOperacao = DataOperacao;
        this.cancelamentoValorTransacaoOriginal = ValorTransacaoOriginal;

        // efetua a operação
        EfetuarOperacao(Operacoes.CANCELAMENTO, promise);
    }

    @ReactMethod
    public void Administrativo(final Promise promise) {
        // efetua a operação
        EfetuarOperacao(Operacoes.ADMINISTRATIVA, promise);
    }

    /**
     * Executa a operação no PayGo
     *
     * @param operacoes {@link Operacoes}
     */
    public void EfetuarOperacao(Operacoes operacoes, final Promise promise) {

        // inicializa a paygo
        InicializaPayGo();

        // configura a transação
        mEntradaTransacao = new EntradaTransacao(operacoes, this.identificacaoAutomacao);

        // informa o tipo da moeda (986 é real)
        mEntradaTransacao.informaCodigoMoeda("986");

        // preenche todas essas informações, se
        if (operacoes != Operacoes.ADMINISTRATIVA) {

            // obtém o tipo do financiamento
            Financiamentos TipoFinanciamento = Financiamentos.obtemFinanciamento(this.tipoFinanciamento);

            // se for venda, informa os dados da venda
            if (operacoes == Operacoes.VENDA) {
                mEntradaTransacao.informaDocumentoFiscal(String.valueOf(this.identificacaoAutomacao));
                mEntradaTransacao.informaValorTotal(this.valorOperacao);
            }

            // se for cancelamento
            if (operacoes == Operacoes.CANCELAMENTO) {
                mEntradaTransacao.informaValorTotal(this.cancelamentoValorTransacaoOriginal);
                mEntradaTransacao.informaNsuTransacaoOriginal(this.cancelamentoNsu);
                mEntradaTransacao.informaCodigoAutorizacaoOriginal(this.cancelamentoCodigoAutorizacao);

                try {
                    mEntradaTransacao.informaDataHoraTransacaoOriginal(this.dateFormat.parse(this.cancelamentoDataOperacao));
                } catch (ParseException e) {
                    promise.reject("INVALID_DATE_FORMAT", "Formato de data esperado 'yyyy-MM-dd' data recebida: " + this.cancelamentoDataOperacao);
                }
            }

            // Define a modalidade de Pagamento
            switch (this.modalidadePagamento) {
                case 0: // ModalidadesPagamento.PAGAMENTO_CARTAO:
                    mEntradaTransacao.informaModalidadePagamento(PAGAMENTO_CARTAO);
                    break;
                case 1: // ModalidadesPagamento.PAGAMENTO_DINHEIRO:
                    mEntradaTransacao.informaModalidadePagamento(PAGAMENTO_DINHEIRO);
                    break;
                case 2: // ModalidadesPagamento.PAGAMENTO_CHEQUE:
                    mEntradaTransacao.informaModalidadePagamento(PAGAMENTO_CHEQUE);
                    break;
                case 3: // ModalidadesPagamento.PAGAMENTO_CARTEIRA_VIRTUAL:
                    mEntradaTransacao.informaModalidadePagamento(PAGAMENTO_CARTEIRA_VIRTUAL);
                    break;
            }

            // Define a Cartão
            mEntradaTransacao.informaTipoCartao(Cartoes.obtemCartao(this.tipoCartao));

            // define o tipo de financiamento
            mEntradaTransacao.informaTipoFinanciamento(TipoFinanciamento);

            // Informa a quantidade de Parcelas
            // só se for parcelado emissor ou estabelecimento
            if (TipoFinanciamento.equals(Financiamentos.PARCELADO_EMISSOR) || TipoFinanciamento.equals(Financiamentos.PARCELADO_ESTABELECIMENTO)) {
                mEntradaTransacao.informaNumeroParcelas(this.parcelas);
            }

            // Informa o adquirente que vai transacionar
            if (!this.adquirente.equals("")) {
                mEntradaTransacao.informaNomeProvedor(this.adquirente);
            }

        }

        // cria a confirmação
        mConfirmacao = new Confirmacoes();

        Log.d(DEBUG_TAG, "CRIANDO THREAD TRANSAÇÃO!");
        new Thread(() -> {
            try {
                mDadosAutomacao.obtemPersonalizacaoCliente();
                mSaidaTransacao = mTransacoes.realizaTransacao(mEntradaTransacao);

                if (mSaidaTransacao == null) {
                    Log.d(DEBUG_TAG, "FALHA - SAIDA TRANSAÇÃO NÃO RETORNOU DADOS!");
                    return;
                }

                mConfirmacao.informaIdentificadorConfirmacaoTransacao(
                    mSaidaTransacao.obtemIdentificadorConfirmacaoTransacao()
                );
            } catch (QuedaConexaoTerminalExcecao e) {

                e.printStackTrace();
                promise.reject(String.valueOf(mSaidaTransacao.obtemResultadoTransacao()), e.getMessage(), e.fillInStackTrace());

            } catch (AplicacaoNaoInstaladaExcecao e) {

              e.printStackTrace();
              promise.reject(String.valueOf(mSaidaTransacao.obtemResultadoTransacao()), e.getMessage(), e.fillInStackTrace());

            }  catch (Exception e) {

                e.printStackTrace();
                promise.reject(String.valueOf(mSaidaTransacao.obtemResultadoTransacao()), e.getMessage(), e.fillInStackTrace());

            } finally {
                mEntradaTransacao = null;

                // json de retorno
                JSONObject json = new JSONObject();
                try {
                    // obtém o resultado da transação
                    int resultado = (mSaidaTransacao != null ? mSaidaTransacao.obtemResultadoTransacao() : -999999);

                    // pega o json com os dados
                    JSONObject json_data = TraduzResultadoOperacao(resultado);

                    // configura o retorno do json
                    json.put("status", true);
                    json.put("resultado", resultado);
                    json.put("data", json_data);

                    // retorna os dados como sucesso
                    promise.resolve(json.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Gera o json com os dados da transação
     */
    private JSONObject TraduzResultadoOperacao(int resultado) throws JSONException {
        JSONObject jsond = new JSONObject();

        jsond.put("resultado", resultado);

        jsond.put("requer_confirmacao", mSaidaTransacao.obtemInformacaoConfirmacao());
        jsond.put("mensagem_retorno", (mSaidaTransacao != null ? mSaidaTransacao.obtemMensagemResultado() : ""));

        jsond.put("numero_parcelas", mSaidaTransacao.obtemNumeroParcelas());

        jsond.put("nsu", mSaidaTransacao.obtemNsuHost());
        jsond.put("codigo_autorizacao", (mSaidaTransacao.obtemCodigoAutorizacao() != null ? mSaidaTransacao.obtemCodigoAutorizacao() : ""));
        jsond.put("codigo_autorizacao_original", (mSaidaTransacao.obtemCodigoAutorizacaoOriginal() != null ? mSaidaTransacao.obtemCodigoAutorizacaoOriginal() : ""));
        jsond.put("data_operacao", (mSaidaTransacao.obtemDataHoraTransacao() != null ? dateTimeFormat.format(mSaidaTransacao.obtemDataHoraTransacao()) : ""));
        jsond.put("documento_fiscal", (mSaidaTransacao.obtemDocumentoFiscal() != null ? mSaidaTransacao.obtemDocumentoFiscal() : ""));

        jsond.put("aid_cartao", (mSaidaTransacao.obtemAidCartao() != null ? mSaidaTransacao.obtemAidCartao() : ""));
        jsond.put("bandeira_cartao", (mSaidaTransacao.obtemNomeCartao() != null ? mSaidaTransacao.obtemNomeCartao() : ""));
        jsond.put("bandeira_cartao_padrao", (mSaidaTransacao.obtemNomeCartaoPadrao() != null ? mSaidaTransacao.obtemNomeCartaoPadrao() : ""));
        jsond.put("nome_portador_cartao", (mSaidaTransacao.obtemNomePortadorCartao() != null ? mSaidaTransacao.obtemNomePortadorCartao() : ""));
        jsond.put("nome_estabelecimento", (mSaidaTransacao.obtemNomeEstabelecimento() != null ? mSaidaTransacao.obtemNomeEstabelecimento() : ""));

        jsond.put("adquirente_nome", (mSaidaTransacao.obtemNomeProvedor() != null ? mSaidaTransacao.obtemNomeProvedor() : ""));
        jsond.put("adquirente_cod_resp", (mSaidaTransacao.obtemCodigoRespostaProvedor() != null ? mSaidaTransacao.obtemCodigoRespostaProvedor() : ""));

        jsond.put("pan_mascarado", (mSaidaTransacao.obtemPanMascarado() != null ? mSaidaTransacao.obtemPanMascarado() : ""));
        jsond.put("pan_mascarado_padrao", (mSaidaTransacao.obtemPanMascaradoPadrao() != null ? mSaidaTransacao.obtemPanMascaradoPadrao() : ""));

        jsond.put("identificador_transacao_automacao", (mSaidaTransacao.obtemIdentificadorTransacaoAutomacao() != null ? mSaidaTransacao.obtemIdentificadorTransacaoAutomacao() : ""));
        jsond.put("identificador_estabelecimento", (mSaidaTransacao.obtemIdentificadorEstabelecimento() != null ? mSaidaTransacao.obtemIdentificadorEstabelecimento() : ""));
        jsond.put("identificador_confirmacao_transacao", (mSaidaTransacao.obtemIdentificadorConfirmacaoTransacao() != null ? mSaidaTransacao.obtemIdentificadorConfirmacaoTransacao() : ""));

        jsond.put("nsu_local_original", (mSaidaTransacao.obtemNsuLocalOriginal() != null ? mSaidaTransacao.obtemNsuLocalOriginal() : ""));
        jsond.put("nsu_local", (mSaidaTransacao.obtemNsuLocal() != null ? mSaidaTransacao.obtemNsuLocal() : ""));
        jsond.put("nsu_host", (mSaidaTransacao.obtemNsuHost() != null ? mSaidaTransacao.obtemNsuHost() : ""));

        jsond.put("modo_verificacao_senha", (mSaidaTransacao.obtemModoVerificacaoSenha() != null ? mSaidaTransacao.obtemModoVerificacaoSenha() : "")); // Falta
        jsond.put("modo_entrada_cartao", (mSaidaTransacao.obtemModoEntradaCartao() != null ? mSaidaTransacao.obtemModoEntradaCartao() : ""));

        jsond.put("tipo_operacao", (mSaidaTransacao.obtemOperacao() != null ? mSaidaTransacao.obtemOperacao() : ""));
        jsond.put("tipo_cartao", (mSaidaTransacao.obtemTipoCartao() != null ? mSaidaTransacao.obtemTipoCartao() : ""));
        jsond.put("tipo_financiamento", (mSaidaTransacao.obtemTipoFinanciamento() != null ? mSaidaTransacao.obtemTipoFinanciamento() : ""));

        jsond.put("ponto_captura", (mSaidaTransacao.obtemIdentificadorPontoCaptura() != null ? mSaidaTransacao.obtemIdentificadorPontoCaptura() : ""));

        // dados da transação pendente
        jsond = TrataValores(jsond);

        // dados da transação pendente
        jsond = TrataDadosTransacaoPendente(jsond);

        // pega os comprovantes
        jsond = TrataComprovantes(jsond);

        return jsond;
    }

    /**
     * Trata os valores da transação.
     */
    private JSONObject TrataValores(JSONObject json) throws JSONException {

        Float valor_total = Float.parseFloat((mSaidaTransacao.obtemValorTotal() != null ? mSaidaTransacao.obtemValorTotal() : "0"));
        Float valor_troco = Float.parseFloat((mSaidaTransacao.obtemValorTroco() != null ? mSaidaTransacao.obtemValorTroco() : "0"));
        Float valor_desconto = Float.parseFloat((mSaidaTransacao.obtemValorDesconto() != null ? mSaidaTransacao.obtemValorDesconto() : "0"));
        Float saldo_voucher = Float.parseFloat((mSaidaTransacao.obtemSaldoVoucher() != null ? mSaidaTransacao.obtemSaldoVoucher() : "0"));

        Float valor_total_f = valor_total / 100;
        Float valor_troco_f = valor_troco / 100;
        Float valor_desconto_f = valor_desconto / 100;
        Float saldo_voucher_f = saldo_voucher / 100;

        json.put("valor_operacao", valor_total_f);
        json.put("valor_troco", valor_troco_f);
        json.put("valor_desconto", valor_desconto_f);

        json.put("saldo_voucher", saldo_voucher_f);

        return json;

    }

    /**
     * Trata os dados da transação pendente.
     */
    private JSONObject TrataDadosTransacaoPendente(JSONObject json) throws JSONException {
        JSONObject json_data = new JSONObject();

        json.put("existe_transacao_pendente", mSaidaTransacao.existeTransacaoPendente());

        if (mSaidaTransacao.existeTransacaoPendente()) {

            if (mSaidaTransacao.obtemDadosTransacaoPendente() != null) {
                TransacaoPendenteDados DadosTrnPend = mSaidaTransacao.obtemDadosTransacaoPendente();

                json_data.put("provedor", (DadosTrnPend.obtemNomeProvedor() != null ? DadosTrnPend.obtemNomeProvedor() : ""));
                json_data.put("identificador_estabelecimento", (DadosTrnPend.obtemIdentificadorEstabelecimento() != null ? DadosTrnPend.obtemIdentificadorEstabelecimento() : ""));
                json_data.put("nsu_local", (DadosTrnPend.obtemNsuLocal() != null ? DadosTrnPend.obtemNsuLocal() : ""));
                json_data.put("nsu_transacao", (DadosTrnPend.obtemNsuTransacao() != null ? DadosTrnPend.obtemNsuTransacao() : ""));
                json_data.put("nsu_host", (DadosTrnPend.obtemNsuLocal() != null ? DadosTrnPend.obtemNsuLocal() : ""));

                json.put("dados_transacao_pendente", json_data);
            }

        }

        return json;

    }

    /**
     * Gera os comprovantes
     */
    private JSONObject TrataComprovantes(JSONObject json) throws JSONException {

        String via_cliente = "";
        if (mSaidaTransacao.obtemComprovanteDiferenciadoPortador() != null) {
            for (String linha : mSaidaTransacao.obtemComprovanteDiferenciadoPortador()) {
                via_cliente += linha;
            }
        }
        json.put("via_cliente", via_cliente);

        // via cliente reduzida
        String via_cliente_reduzida = "";
        if (mSaidaTransacao.obtemComprovanteReduzidoPortador() != null) {
            for (String linha : mSaidaTransacao.obtemComprovanteReduzidoPortador()) {
                via_cliente_reduzida += linha;
            }
        }
        json.put("via_cliente_reduzida", via_cliente_reduzida);

        // via estabelecimento
        String via_estabelecimento = "";
        if (mSaidaTransacao.obtemComprovanteDiferenciadoLoja() != null) {
            for (String linha : mSaidaTransacao.obtemComprovanteDiferenciadoLoja()) {
                via_estabelecimento += linha;
            }
        }
        json.put("via_estabelecimento", via_estabelecimento);

        // via cupom full
        String via_cupom_full = "";
        if (mSaidaTransacao.obtemComprovanteCompleto() != null) {
            for (String linha : mSaidaTransacao.obtemComprovanteCompleto()) {
                via_cupom_full += linha;
            }
        }
        json.put("via_cupom_full", via_cupom_full);

        // comprovantes graficos
        if (this.retornaComprovantesGraficos) {
            json.put("via_cliente_grafico", (mSaidaTransacao.comprovanteGraficoDisponivel() ? mSaidaTransacao.obtemComprovanteGraficoPortador() : ""));
            json.put("via_estabelecimento_grafico", (mSaidaTransacao.comprovanteGraficoDisponivel() ? mSaidaTransacao.obtemComprovanteGraficoLojista() : ""));
        }

        return json;
    }

    /**
     * Inicializa a configuração da PayGo.
     */
    private void InicializaPayGo() {
        // configura a personalizacao
        mPersonalizacao = setPersonalizacao();

        // cria o objeto dos dados da automação
        mDadosAutomacao = new DadosAutomacao(
            this.empresaAutomacao,
            this.nomeAutomacao,
            this.versaoAutomacao,
            this.suportaTroco,
            this.suportaDesconto,
            this.suportaViasDiferenciadas,
            this.suportaViaReduzida,
            mPersonalizacao
        );

        // cria a instância de transações
        mTransacoes = Transacoes.obtemInstancia(mDadosAutomacao, this.appContext);
    }

    /**
     * Configura a personalização (tema) do PayGo Integrado
     *
     * @return Personalizacao
     */
    public Personalizacao setPersonalizacao() {
        Personalizacao.Builder PersonalizacaoBuilder = new Personalizacao.Builder();

        try {
            if (!TextUtils.isEmpty(this.fileIconDestino)) {
                PersonalizacaoBuilder.informaIconeToolbar(new File(this.fileIconDestino));
            }
            if (!TextUtils.isEmpty(this.fileFonteDestino)) {
                PersonalizacaoBuilder.informaFonte(new File(this.fileFonteDestino));
            }
            if (!TextUtils.isEmpty(this.informaCorFonte)) {
                PersonalizacaoBuilder.informaCorFonte(this.informaCorFonte);
            }
            if (!TextUtils.isEmpty(this.informaCorFonteTeclado)) {
                PersonalizacaoBuilder.informaCorFonteTeclado(this.informaCorFonteTeclado);
            }
            if (!TextUtils.isEmpty(this.informaCorFundoCaixaEdicao)) {
                PersonalizacaoBuilder.informaCorFundoCaixaEdicao(this.informaCorFundoCaixaEdicao);
            }
            if (!TextUtils.isEmpty(this.informaCorFundoTela)) {
                PersonalizacaoBuilder.informaCorFundoTela(this.informaCorFundoTela);
            }
            if (!TextUtils.isEmpty(this.informaCorFundoTeclado)) {
                PersonalizacaoBuilder.informaCorFundoTeclado(this.informaCorFundoTeclado);
            }
            if (!TextUtils.isEmpty(this.informaCorFundoToolbar)) {
                PersonalizacaoBuilder.informaCorFundoToolbar(this.informaCorFundoToolbar);
            }
            if (!TextUtils.isEmpty(this.informaCorTextoCaixaEdicao)) {
                PersonalizacaoBuilder.informaCorTextoCaixaEdicao(this.informaCorTextoCaixaEdicao);
            }
            if (!TextUtils.isEmpty(this.informaCorTeclaPressionadaTeclado)) {
                PersonalizacaoBuilder.informaCorTeclaPressionadaTeclado(this.informaCorTeclaPressionadaTeclado);
            }
            if (!TextUtils.isEmpty(this.informaCorTeclaLiberadaTeclado)) {
                PersonalizacaoBuilder.informaCorTeclaLiberadaTeclado(this.informaCorTeclaLiberadaTeclado);
            }
            if (!TextUtils.isEmpty(this.informaCorSeparadorMenu)) {
                PersonalizacaoBuilder.informaCorSeparadorMenu(this.informaCorSeparadorMenu);
            }
        } catch (IllegalArgumentException e) {
            Toast.makeText(appContext, "Verifique valores de\nconfiguração da personalização", Toast.LENGTH_SHORT).show();
            throw e;
        }

        return PersonalizacaoBuilder.build();
    }

}

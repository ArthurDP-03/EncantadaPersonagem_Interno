import { useCallback, useEffect, useState } from "react";
import Swal from "sweetalert2";
import { getEventoById, atualizarEvento } from "../services/eventosService";
import { listarPersonagensDoEvento } from "../services/eventoPersonagemService";
import { listarPorEventoPersonagem } from "../services/conviteService";
import { getEscalacoesByEventoId } from "../services/escalacaoService";

const eventoInicial = {
  titulo: "",
  descricao: "",
  dataInicio: "",
  dataFim: "",
  endereco: "",
  status: "",
  tipoPagamento: "",
  valorTotal: "",
  clienteId: "",
  administradorCriadorId: "",
};

const formatarDataInput = (value) => (value ? String(value).slice(0, 16) : "");

const normalizarEventoParaForm = (evento) => ({
  titulo: evento.titulo ?? "",
  descricao: evento.descricao ?? "",
  dataInicio: formatarDataInput(evento.dataInicio),
  dataFim: formatarDataInput(evento.dataFim),
  endereco: evento.endereco ?? "",
  status: evento.status ?? "",
  tipoPagamento: evento.tipoPagamento ?? "",
  valorTotal: evento.valorTotal ?? "",
  clienteId: evento.clienteId ?? "",
  administradorCriadorId: evento.administradorCriadorId ?? "",
});

const montarPayloadEvento = (form) => ({
  ...form,
  valorTotal: Number(form.valorTotal),
  clienteId: Number(form.clienteId),
  administradorCriadorId: Number(form.administradorCriadorId),
});

const obterMensagemErro = (err, fallback) =>
  err.data?.message || err.data?.error || fallback;

export function useEventoDetalhes(eventoId) {
  const [evento, setEvento] = useState(null);
  const [form, setForm] = useState(eventoInicial);
  const [personagensEvento, setPersonagensEvento] = useState([]);
  const [escalacoes, setEscalacoes] = useState([]);
  const [convites, setConvites] = useState([]);
  const [editando, setEditando] = useState(false);
  const [carregando, setCarregando] = useState(true);
  const [salvando, setSalvando] = useState(false);
  const [erro, setErro] = useState(null);

  const carregarDados = useCallback(async () => {
    if (!eventoId) return;

    try {
      setCarregando(true);
      setErro(null);

      const [eventoAtual, personagens, escalacoesEvento] = await Promise.all([
        getEventoById(eventoId),
        listarPersonagensDoEvento(eventoId),
        getEscalacoesByEventoId(eventoId),
      ]);

      const convitesPorPersonagem = await Promise.all(
        personagens.map((personagem) => listarPorEventoPersonagem(personagem.id))
      );

      setEvento(eventoAtual);
      setForm(normalizarEventoParaForm(eventoAtual));
      setPersonagensEvento(personagens);
      setEscalacoes(escalacoesEvento);
      setConvites(convitesPorPersonagem.flat());
    } catch (err) {
      console.error("Erro ao carregar detalhes do evento:", err);
      const mensagem = obterMensagemErro(err, "Erro ao carregar detalhes do evento");
      setErro(mensagem);
      Swal.fire({
        icon: "error",
        title: "Não foi possível carregar o evento",
        text: mensagem,
      });
    } finally {
      setCarregando(false);
    }
  }, [eventoId]);

  useEffect(() => {
    const timeoutId = window.setTimeout(() => {
      carregarDados();
    }, 0);

    return () => window.clearTimeout(timeoutId);
  }, [carregarDados]);

  const iniciarEdicao = () => setEditando(true);

  const cancelarEdicao = () => {
    if (evento) {
      setForm(normalizarEventoParaForm(evento));
    }
    setEditando(false);
  };

  const salvarEvento = async (event) => {
    event.preventDefault();

    if (
      !String(form.titulo ?? "").trim() ||
      !String(form.dataInicio ?? "").trim() ||
      !String(form.dataFim ?? "").trim() ||
      !String(form.clienteId ?? "").trim()
    ) {
      Swal.fire({
        icon: "warning",
        title: "Campos obrigatórios",
        text: "Preencha título, data de início, data de fim e cliente antes de salvar.",
      });
      return;
    }

    try {
      setSalvando(true);
      const atualizado = await atualizarEvento(eventoId, montarPayloadEvento(form));

      setEvento(atualizado);
      setForm(normalizarEventoParaForm(atualizado));
      setEditando(false);
      Swal.fire({
        icon: "success",
        title: "Dados do evento salvos",
        timer: 1800,
        showConfirmButton: false,
      });
    } catch (err) {
      console.error("Erro ao salvar evento:", err);
      Swal.fire({
        icon: "error",
        title: "Não foi possível salvar o evento",
        text: obterMensagemErro(err, "Verifique os dados e tente novamente"),
      });
    } finally {
      setSalvando(false);
    }
  };

  return {
    evento,
    form,
    setForm,
    personagensEvento,
    escalacoes,
    convites,
    editando,
    carregando,
    salvando,
    erro,
    iniciarEdicao,
    cancelarEdicao,
    salvarEvento,
  };
}

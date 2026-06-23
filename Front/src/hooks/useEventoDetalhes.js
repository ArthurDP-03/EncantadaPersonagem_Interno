import { useCallback, useEffect, useState } from "react";
import Swal from "sweetalert2";
import { getEventoById, atualizarEvento } from "../services/eventosService";
import {
  adicionarPersonagemAoEvento,
  listarPersonagensDoEvento,
  removerPersonagemDoEvento,
} from "../services/eventoPersonagemService";
import { getPersonagemItens } from "../services/personagemItemService";
import {
  cancelarConvite,
  enviarConvites,
  listarPorEventoPersonagem,
} from "../services/conviteService";
import { escolherAtorFinal, getEscalacoesByEventoId } from "../services/escalacaoService";
import { getAtores } from "../services/atoresService";

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
  administradorCriadorNome: "",
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
  administradorCriadorNome: evento.administradorCriadorNome ?? "",
});

const montarPayloadEvento = (form) => {
  const payload = { ...form };
  delete payload.administradorCriadorNome;

  return {
    ...payload,
    valorTotal: Number(form.valorTotal),
    clienteId: Number(form.clienteId),
  };
};

const obterMensagemErro = (err, fallback) =>
  err.data?.message || err.data?.error || fallback;

const filtrarItensDisponiveis = (itens, personagensEvento) => {
  const itensJaVinculados = new Set(
    personagensEvento.map((personagem) => personagem.personagemItemId)
  );

  return itens.filter(
    (item) => item.status === "DISPONIVEL" && !itensJaVinculados.has(item.id)
  );
};

export function useEventoDetalhes(eventoId) {
  const [evento, setEvento] = useState(null);
  const [form, setForm] = useState(eventoInicial);
  const [personagensEvento, setPersonagensEvento] = useState([]);
  const [personagemItensDisponiveis, setPersonagemItensDisponiveis] = useState([]);
  const [escalacoes, setEscalacoes] = useState([]);
  const [convites, setConvites] = useState([]);
  const [atores, setAtores] = useState([]);
  const [editando, setEditando] = useState(false);
  const [carregando, setCarregando] = useState(true);
  const [salvando, setSalvando] = useState(false);
  const [adicionandoPersonagemItemId, setAdicionandoPersonagemItemId] = useState(null);
  const [removendoEventoPersonagemId, setRemovendoEventoPersonagemId] = useState(null);
  const [processandoConviteId, setProcessandoConviteId] = useState(null);
  const [enviandoConvitesEventoPersonagemId, setEnviandoConvitesEventoPersonagemId] = useState(null);
  const [escalandoConviteId, setEscalandoConviteId] = useState(null);
  const [erro, setErro] = useState(null);

  const carregarDados = useCallback(async () => {
    if (!eventoId) return;

    try {
      setCarregando(true);
      setErro(null);

      const [eventoAtual, personagens, escalacoesEvento, personagemItens, atoresEvento] = await Promise.all([
        getEventoById(eventoId),
        listarPersonagensDoEvento(eventoId),
        getEscalacoesByEventoId(eventoId),
        getPersonagemItens(),
        getAtores(),
      ]);

      const convitesPorPersonagem = await Promise.all(
        personagens.map((personagem) => listarPorEventoPersonagem(personagem.id))
      );

      setEvento(eventoAtual);
      setForm(normalizarEventoParaForm(eventoAtual));
      setPersonagensEvento(personagens);
      setPersonagemItensDisponiveis(filtrarItensDisponiveis(personagemItens, personagens));
      setEscalacoes(escalacoesEvento);
      setConvites(convitesPorPersonagem.flat());
      setAtores(atoresEvento);
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

  const adicionarPersonagemItem = async (personagemItemId) => {
    try {
      setAdicionandoPersonagemItemId(personagemItemId);
      await adicionarPersonagemAoEvento({
        eventoId,
        personagemItemId,
      });
      await carregarDados();

      Swal.fire({
        icon: "success",
        title: "Personagem adicionado ao evento",
        timer: 1800,
        showConfirmButton: false,
      });
      return true;
    } catch (err) {
      console.error("Erro ao adicionar personagem ao evento:", err);
      Swal.fire({
        icon: "error",
        title: "Não foi possível adicionar o personagem",
        text: obterMensagemErro(err, "Verifique a disponibilidade do item e tente novamente"),
      });
      return false;
    } finally {
      setAdicionandoPersonagemItemId(null);
    }
  };

  const removerPersonagemItem = async (personagemEvento) => {
    const resultado = await Swal.fire({
      icon: "warning",
      title: "Remover personagem do evento?",
      text: `${personagemEvento.personagemNome} (${personagemEvento.personagemItemCodigo}) voltará a ficar disponível.`,
      showCancelButton: true,
      confirmButtonText: "Remover",
      cancelButtonText: "Cancelar",
      confirmButtonColor: "#c97b7b",
    });

    if (!resultado.isConfirmed) return false;

    try {
      setRemovendoEventoPersonagemId(personagemEvento.id);
      await removerPersonagemDoEvento(personagemEvento.id);
      await carregarDados();

      Swal.fire({
        icon: "success",
        title: "Personagem removido do evento",
        timer: 1800,
        showConfirmButton: false,
      });
      return true;
    } catch (err) {
      console.error("Erro ao remover personagem do evento:", err);
      Swal.fire({
        icon: "error",
        title: "Não foi possível remover o personagem",
        text: obterMensagemErro(err, "Verifique se não existe escalação para este personagem"),
      });
      return false;
    } finally {
      setRemovendoEventoPersonagemId(null);
    }
  };

  const adicionarConvitesPersonagem = async (personagemEvento, atoresIds) => {
    if (!personagemEvento || atoresIds.length === 0) return false;

    try {
      setEnviandoConvitesEventoPersonagemId(personagemEvento.id);
      await enviarConvites({
        eventoPersonagemId: personagemEvento.id,
        convites: atoresIds.map((atorId) => ({
          atorId,
          personagemItemId: personagemEvento.personagemItemId,
        })),
      });
      await carregarDados();

      Swal.fire({
        icon: "success",
        title: "Convites enviados",
        timer: 1800,
        showConfirmButton: false,
      });
      return true;
    } catch (err) {
      console.error("Erro ao enviar convites:", err);
      Swal.fire({
        icon: "error",
        title: "Não foi possível enviar os convites",
        text: obterMensagemErro(err, "Verifique os atores selecionados e tente novamente"),
      });
      return false;
    } finally {
      setEnviandoConvitesEventoPersonagemId(null);
    }
  };

  const excluirConvitePersonagem = async (convite) => {
    const resultado = await Swal.fire({
      icon: "warning",
      title: "Excluir convite?",
      text: `${convite.atorNome} deixará de ter este convite ativo.`,
      showCancelButton: true,
      confirmButtonText: "Excluir",
      cancelButtonText: "Cancelar",
      confirmButtonColor: "#c97b7b",
    });

    if (!resultado.isConfirmed) return false;

    try {
      setProcessandoConviteId(convite.id);
      await cancelarConvite(convite.id);
      await carregarDados();

      Swal.fire({
        icon: "success",
        title: "Convite excluído",
        timer: 1800,
        showConfirmButton: false,
      });
      return true;
    } catch (err) {
      console.error("Erro ao excluir convite:", err);
      Swal.fire({
        icon: "error",
        title: "Não foi possível excluir o convite",
        text: obterMensagemErro(err, "Apenas convites pendentes podem ser cancelados"),
      });
      return false;
    } finally {
      setProcessandoConviteId(null);
    }
  };

  const adicionarConviteNaEscalacao = async (convite) => {
    try {
      setEscalandoConviteId(convite.id);
      await escolherAtorFinal({
        eventoPersonagemId: convite.eventoPersonagemId,
        atorId: convite.atorId,
        personagemItemId: convite.personagemItemId,
      });
      await carregarDados();

      Swal.fire({
        icon: "success",
        title: "Ator adicionado à escalação",
        timer: 1800,
        showConfirmButton: false,
      });
      return true;
    } catch (err) {
      console.error("Erro ao adicionar à escalação:", err);
      Swal.fire({
        icon: "error",
        title: "Não foi possível adicionar à escalação",
        text: obterMensagemErro(err, "Verifique se o convite foi aceito e tente novamente"),
      });
      return false;
    } finally {
      setEscalandoConviteId(null);
    }
  };

  return {
    evento,
    form,
    setForm,
    personagensEvento,
    personagemItensDisponiveis,
    escalacoes,
    convites,
    atores,
    editando,
    carregando,
    salvando,
    adicionandoPersonagemItemId,
    removendoEventoPersonagemId,
    processandoConviteId,
    enviandoConvitesEventoPersonagemId,
    escalandoConviteId,
    erro,
    iniciarEdicao,
    cancelarEdicao,
    salvarEvento,
    adicionarPersonagemItem,
    removerPersonagemItem,
    adicionarConvitesPersonagem,
    excluirConvitePersonagem,
    adicionarConviteNaEscalacao,
  };
}

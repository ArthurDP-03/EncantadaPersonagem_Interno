import { useState, useEffect } from "react";
import Swal from "sweetalert2";
import i18n from "../i18n";
import { getAtores, deletarAtor, criarAtor, atualizarAtor } from "../services/atoresService";

const t = i18n.t.bind(i18n);

const escapeHtml = (value) =>
  String(value)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#39;");

const formatarCamposValidacao = (fields) => {
  const itens = Object.entries(fields)
    .map(([campo, msg]) => `<li><strong>${escapeHtml(campo)}</strong>: ${escapeHtml(msg)}</li>`)
    .join("");

  return `<div style="text-align:left;"><ul style="margin:0;padding-left:1.25rem;">${itens}</ul></div>`;
};

const obterMensagemErro = (err, fallback) =>
  err.data?.message || err.data?.error || fallback;

const mostrarErroGenerico = (err, title, fallback) => {
  Swal.fire({
    icon: "error",
    title,
    text: obterMensagemErro(err, fallback),
  });
};

const mostrarErroValidacaoOuGenerico = (err, title, fallback) => {
  if (err.status === 422 && err.data?.fields) {
    Swal.fire({
      icon: "error",
      title: t('common.validation.invalidData'),
      html: formatarCamposValidacao(err.data.fields),
    });
    return;
  }

  mostrarErroGenerico(err, title, fallback);
};

export function useAtores() {
  const [atores, setAtores] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);

  useEffect(() => {
    getAtores()
      .then(setAtores)
      .catch(err => {
        console.error("Erro completo:", err);
        const mensagemErro = obterMensagemErro(err, t('collaborators.actors.errors.load'));
        setErro(mensagemErro);
        Swal.fire({ icon: "error", title: t('collaborators.actors.titles.load'), text: mensagemErro });
      })
      .finally(() => setCarregando(false));
  }, []);

  const deletar = async (id) => {
    const resultado = await Swal.fire({
      title: t('common.confirm.deleteTitle', { item: t('common.items.actor').toLowerCase() }),
      text: t('common.confirm.deleteText'),
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: t('common.confirm.confirmBtn'),
      cancelButtonText: t('common.confirm.cancelBtn'),
      confirmButtonColor: "#d33"
    });
    if (!resultado.isConfirmed) return;
    try {
      await deletarAtor(id);
      setAtores(prev => prev.filter(a => a.id !== id));
      Swal.fire({ icon: "success", title: t('common.success.deleted', { item: t('common.items.actor') }), timer: 1800, showConfirmButton: false });
    } catch (err) {
      console.error("Erro ao deletar ator:", err);
      mostrarErroGenerico(err, t('collaborators.actors.titles.delete'), t('collaborators.actors.errors.delete'));
    }
  };

  const criar = async (dados) => {
    try {
      const novoAtor = await criarAtor(dados);
      setAtores(prev => [...prev, novoAtor]);
      Swal.fire({ icon: "success", title: t('common.success.created', { item: t('common.items.actor') }), timer: 1800, showConfirmButton: false });
    } catch (err) {
      console.error("Erro ao criar ator:", err);
      mostrarErroValidacaoOuGenerico(err, t('collaborators.actors.titles.create'), t('collaborators.actors.errors.create'));
    }
  };

  const editar = async (id, dados) => {
    try {
      const atualizado = await atualizarAtor(id, dados);
      setAtores(prev => prev.map(a => a.id === id ? atualizado : a));
      Swal.fire({ icon: "success", title: t('common.success.updated', { item: t('common.items.actor') }), timer: 1800, showConfirmButton: false });
    } catch (err) {
      console.error("Erro ao editar ator:", err);
      mostrarErroValidacaoOuGenerico(err, t('collaborators.actors.titles.update'), t('collaborators.actors.errors.update'));
    }
  };

  return { atores, carregando, erro, deletar, criar, editar };
}

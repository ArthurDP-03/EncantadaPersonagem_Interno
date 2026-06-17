import { useState, useEffect } from "react";
import Swal from "sweetalert2";
import i18n from "../i18n";
import {
  getEventos,
  criarEvento,
  atualizarEvento,
  deletarEvento,
} from "../services/eventosService";

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

export function useEventos() {
  const [eventos, setEventos] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);

  useEffect(() => {
    getEventos()
      .then(setEventos)
      .catch(err => {
        console.error("Erro completo:", err);
        const mensagemErro = obterMensagemErro(err, t('events.errors.load'));
        setErro(mensagemErro);
        Swal.fire({
          icon: "error",
          title: t('events.titles.load'),
          text: mensagemErro,
        });
      })
      .finally(() => setCarregando(false));
  }, []);

  const deletar = async (id) => {
    const resultado = await Swal.fire({
      title: t('common.confirm.deleteTitle', { item: t('common.items.event').toLowerCase() }),
      text: t('common.confirm.deleteText'),
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: t('common.confirm.confirmBtn'),
      cancelButtonText: t('common.confirm.cancelBtn'),
      confirmButtonColor: "#d33",
    });

    if (!resultado.isConfirmed) return;

    try {
      await deletarEvento(id);
      setEventos(prev => prev.filter(e => e.id !== id));
      Swal.fire({ icon: "success", title: t('common.success.deleted', { item: t('common.items.event') }), timer: 1800, showConfirmButton: false });
    } catch (err) {
      console.error("Erro ao deletar evento:", err);
      mostrarErroGenerico(err, t('events.titles.delete'), t('events.errors.delete'));
    }
  };

  const criar = async (dados) => {
    try {
      const novoEvento = await criarEvento(dados);
      setEventos(prev => [...prev, novoEvento]);
      Swal.fire({ icon: "success", title: t('common.success.created', { item: t('common.items.event') }), timer: 1800, showConfirmButton: false });
    } catch (err) {
      console.error("Erro ao criar evento:", err);
      mostrarErroValidacaoOuGenerico(err, t('events.titles.create'), t('events.errors.create'));

      throw err;
    }
  };

  const editar = async (id, dados) => {
    try {
      const atualizado = await atualizarEvento(id, dados);
      setEventos(prev => prev.map(e => (e.id === id ? atualizado : e)));
      Swal.fire({ icon: "success", title: t('common.success.updated', { item: t('common.items.event') }), timer: 1800, showConfirmButton: false });
    } catch (err) {
      console.error("Erro ao editar evento:", err);
      mostrarErroValidacaoOuGenerico(err, t('events.titles.update'), t('events.errors.update'));

      throw err;
    }
  };

  return { eventos, carregando, erro, criar, editar, deletar };
}

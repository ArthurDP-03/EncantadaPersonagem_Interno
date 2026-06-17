import { useState, useEffect } from "react";
import Swal from "sweetalert2";
import i18n from "../i18n";
import {
  getPersonagemItens,
  criarPersonagemItem,
  atualizarStatusPersonagemItem,
  deletarPersonagemItem,
} from "../services/personagemItemService";

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

export function usePersonagemItens() {
  const [personagemItens, setPersonagemItens] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);

  const carregarPersonagemItens = () => {
    getPersonagemItens()
      .then(setPersonagemItens)
      .catch(err => {
        console.error("Erro completo:", err);
        const mensagemErro = obterMensagemErro(err, t('characterItems.errors.load'));
        setErro(mensagemErro);

        Swal.fire({
          icon: "error",
          title: t('characterItems.titles.load'),
          text: mensagemErro
        });
      })
      .finally(() => setCarregando(false));
  };

  useEffect(() => {
    carregarPersonagemItens();
  }, []);

  const criar = async (personagemItem) => {
    try {
      await criarPersonagemItem(personagemItem);
      carregarPersonagemItens();

      Swal.fire({
        icon: "success",
        title: t('characterItems.success.created'),
        timer: 1800,
        showConfirmButton: false
      });
    } catch (err) {
      console.error("Erro ao criar item de personagem:", err);

      mostrarErroValidacaoOuGenerico(err, t('characterItems.titles.create'), t('characterItems.errors.create'));

      throw err;
    }
  };

  const atualizarStatus = async (id, personagemItem) => {
    try {
      await atualizarStatusPersonagemItem(id, personagemItem);
      carregarPersonagemItens();

      Swal.fire({
        icon: "success",
        title: t('characterItems.success.statusUpdated'),
        timer: 1800,
        showConfirmButton: false
      });
    } catch (err) {
      console.error("Erro ao atualizar status do item de personagem:", err);

      mostrarErroValidacaoOuGenerico(
        err,
        t('characterItems.titles.updateStatus'),
        t('characterItems.errors.updateStatus')
      );

      throw err;
    }
  };

  const deletar = async (id) => {
    const resultado = await Swal.fire({
      title: t('characterItems.confirm.deleteTitle'),
      text: t('characterItems.confirm.deleteText'),
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: t('characterItems.confirm.confirmBtn'),
      cancelButtonText: t('characterItems.confirm.cancelBtn'),
      confirmButtonColor: "#d33"
    });

    if (!resultado.isConfirmed) return;

    try {
      await deletarPersonagemItem(id);
      carregarPersonagemItens();

      Swal.fire({
        icon: "success",
        title: t('characterItems.success.deleted'),
        timer: 1800,
        showConfirmButton: false
      });
    } catch (err) {
      console.error("Erro ao deletar item de personagem:", err);

      mostrarErroGenerico(err, t('characterItems.titles.delete'), t('characterItems.errors.delete'));
    }
  };

  return { personagemItens, carregando, erro, criar, atualizarStatus, deletar };
}

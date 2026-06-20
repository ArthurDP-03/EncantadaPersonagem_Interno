import { useState, useEffect } from "react";
import Swal from "sweetalert2";
import i18n from "../i18n";
import { getPersonagens, criarPersonagem, atualizarPersonagem, deletarPersonagem } from "../services/personagensService";

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

export function usePersonagens() {
  const [personagens, setPersonagens] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);

  const carregarPersonagens = () => {
    getPersonagens()
      .then(setPersonagens)
      .catch(err => {
        console.error("Erro completo:", err);
        const mensagemErro = obterMensagemErro(err, t('characters.errors.load'));
        setErro(mensagemErro);

        Swal.fire({
          icon: "error",
          title: t('characters.titles.load'),
          text: mensagemErro
        });
      })
      .finally(() => setCarregando(false));
  };

  useEffect(() => {
    carregarPersonagens();
  }, []);

  const criar = async (personagem) => {
    try {
      await criarPersonagem(personagem);
      carregarPersonagens();

      Swal.fire({
        icon: "success",
        title: t('common.success.created', { item: t('common.items.character') }),
        timer: 1800,
        showConfirmButton: false
      });
    } catch (err) {
      console.error("Erro ao criar personagem:", err);

      mostrarErroValidacaoOuGenerico(err, t('characters.titles.create'), t('characters.errors.create'));

      throw err;
    }
  };

  const editar = async (id, personagem) => {
    try {
      await atualizarPersonagem(id, personagem);
      carregarPersonagens();

      Swal.fire({
        icon: "success",
        title: t('common.success.updated', { item: t('common.items.character') }),
        timer: 1800,
        showConfirmButton: false
      });
    } catch (err) {
      console.error("Erro ao editar personagem:", err);

      mostrarErroValidacaoOuGenerico(err, t('characters.titles.update'), t('characters.errors.update'));

      throw err;
    }
  };

  const deletar = async (id) => {
    const resultado = await Swal.fire({
      title: t('common.confirm.deleteTitle', { item: t('common.items.character').toLowerCase() }),
      text: t('common.confirm.deleteText'),
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: t('common.confirm.confirmBtn'),
      cancelButtonText: t('common.confirm.cancelBtn'),
      confirmButtonColor: "#d33"
    });

    if (!resultado.isConfirmed) return;

    try {
      await deletarPersonagem(id);
      carregarPersonagens();

      Swal.fire({
        icon: "success",
        title: t('common.success.deleted', { item: t('common.items.character') }),
        timer: 1800,
        showConfirmButton: false
      });
    } catch (err) {
      console.error("Erro ao deletar personagem:", err);

      mostrarErroGenerico(err, t('characters.titles.delete'), t('characters.errors.delete'));
    }
  };

  return { personagens, carregando, erro, criar, editar, deletar };
}

import { useState, useEffect } from "react";
import Swal from "sweetalert2";
import {
  getPersonagemItens,
  criarPersonagemItem,
  atualizarStatusPersonagemItem,
  deletarPersonagemItem,
} from "../services/personagemItemService";

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
      title: "Dados inválidos",
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
        const mensagemErro = obterMensagemErro(err, "Erro ao carregar itens de personagem");
        setErro(mensagemErro);

        Swal.fire({
          icon: "error",
          title: "Erro ao carregar",
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
        title: "Item criado",
        timer: 1800,
        showConfirmButton: false
      });
    } catch (err) {
      console.error("Erro ao criar item de personagem:", err);

      mostrarErroValidacaoOuGenerico(err, "Erro ao criar", "Não foi possível criar o item de personagem");

      throw err;
    }
  };

  const atualizarStatus = async (id, personagemItem) => {
    try {
      await atualizarStatusPersonagemItem(id, personagemItem);
      carregarPersonagemItens();

      Swal.fire({
        icon: "success",
        title: "Status atualizado",
        timer: 1800,
        showConfirmButton: false
      });
    } catch (err) {
      console.error("Erro ao atualizar status do item de personagem:", err);

      mostrarErroValidacaoOuGenerico(
        err,
        "Erro ao atualizar",
        "Não foi possível atualizar o status do item"
      );

      throw err;
    }
  };

  const deletar = async (id) => {
    const resultado = await Swal.fire({
      title: "Deseja deletar este item de personagem?",
      text: "Esta ação não poderá ser desfeita.",
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "Deletar",
      cancelButtonText: "Cancelar",
      confirmButtonColor: "#d33"
    });

    if (!resultado.isConfirmed) return;

    try {
      await deletarPersonagemItem(id);
      carregarPersonagemItens();

      Swal.fire({
        icon: "success",
        title: "Item deletado",
        timer: 1800,
        showConfirmButton: false
      });
    } catch (err) {
      console.error("Erro ao deletar item de personagem:", err);

      mostrarErroGenerico(err, "Erro ao deletar", "Não foi possível deletar o item de personagem");
    }
  };

  return { personagemItens, carregando, erro, criar, atualizarStatus, deletar };
}

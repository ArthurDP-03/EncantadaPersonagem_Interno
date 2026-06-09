import { useState, useEffect } from "react";
import Swal from "sweetalert2";
import { getPersonagens, criarPersonagem, atualizarPersonagem, deletarPersonagem } from "../services/personagensService";

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

export function usePersonagens() {
  const [personagens, setPersonagens] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);

  const carregarPersonagens = () => {
    getPersonagens()
      .then(setPersonagens)
      .catch(err => {
        console.error("Erro completo:", err);
        const mensagemErro = obterMensagemErro(err, "Erro ao carregar personagens");
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
    carregarPersonagens();
  }, []);

  const criar = async (personagem) => {
    try {
      await criarPersonagem(personagem);
      carregarPersonagens();

      Swal.fire({
        icon: "success",
        title: "Personagem criado",
        timer: 1800,
        showConfirmButton: false
      });
    } catch (err) {
      console.error("Erro ao criar personagem:", err);

      mostrarErroValidacaoOuGenerico(err, "Erro ao criar", "Não foi possível criar o personagem");

      throw err;
    }
  };

  const editar = async (id, personagem) => {
    try {
      await atualizarPersonagem(id, personagem);
      carregarPersonagens();

      Swal.fire({
        icon: "success",
        title: "Personagem atualizado",
        timer: 1800,
        showConfirmButton: false
      });
    } catch (err) {
      console.error("Erro ao editar personagem:", err);

      mostrarErroValidacaoOuGenerico(err, "Erro ao atualizar", "Não foi possível atualizar o personagem");

      throw err;
    }
  };

  const deletar = async (id) => {
    const resultado = await Swal.fire({
      title: "Deseja deletar este personagem?",
      text: "Esta ação não poderá ser desfeita.",
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "Deletar",
      cancelButtonText: "Cancelar",
      confirmButtonColor: "#d33"
    });

    if (!resultado.isConfirmed) return;

    try {
      await deletarPersonagem(id);
      carregarPersonagens();

      Swal.fire({
        icon: "success",
        title: "Personagem deletado",
        timer: 1800,
        showConfirmButton: false
      });
    } catch (err) {
      console.error("Erro ao deletar personagem:", err);

      mostrarErroGenerico(err, "Erro ao deletar", "Não foi possível deletar o personagem");
    }
  };

  return { personagens, carregando, erro, criar, editar, deletar };
}

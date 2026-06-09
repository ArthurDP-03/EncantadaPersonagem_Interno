import { useState, useEffect } from "react";
import Swal from "sweetalert2";
import { getAdministrador, deletarAdministrador, criarAdministrador, atualizarAdministrador } from "../services/administradoresService";

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

export function useAdministradores() {
  const [administradores, setAdministradores] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);

  useEffect(() => {
    getAdministrador()
      .then(setAdministradores)
      .catch(err => {
        console.error("Erro completo:", err);
        const mensagemErro = obterMensagemErro(err, "Erro ao carregar administradores");
        setErro(mensagemErro);
        Swal.fire({ icon: "error", title: "Erro ao carregar", text: mensagemErro });
      })
      .finally(() => setCarregando(false));
  }, []);

  const deletar = async (id) => {
    const resultado = await Swal.fire({ title: "Deseja deletar este administrador?", text: "Esta ação não poderá ser desfeita.", icon: "warning", showCancelButton: true, confirmButtonText: "Deletar", cancelButtonText: "Cancelar", confirmButtonColor: "#d33" });
    if (!resultado.isConfirmed) return;
    try {
      await deletarAdministrador(id);
      setAdministradores(prev => prev.filter(a => a.id !== id));
      Swal.fire({ icon: "success", title: "Administrador deletado", timer: 1800, showConfirmButton: false });
    } catch (err) {
      console.error("Erro ao deletar administrador:", err);
      mostrarErroGenerico(err, "Erro ao deletar", "Não foi possível deletar o administrador");
    }
  };

  const criar = async (dados) => {
    try {
      const novoAdministrador = await criarAdministrador(dados);
      setAdministradores(prev => [...prev, novoAdministrador]);
      Swal.fire({ icon: "success", title: "Administrador criado", timer: 1800, showConfirmButton: false });
    } catch (err) {
      console.error("Erro ao criar administrador:", err);
      mostrarErroValidacaoOuGenerico(err, "Erro ao criar", "Não foi possível criar o administrador");
    }
  };

  const editar = async (id, dados) => {
    try {
      const atualizado = await atualizarAdministrador(id, dados);
      setAdministradores(prev => prev.map(a => a.id === id ? atualizado : a));
      Swal.fire({ icon: "success", title: "Administrador atualizado", timer: 1800, showConfirmButton: false });
    } catch (err) {
      console.error("Erro ao editar administrador:", err);
      mostrarErroValidacaoOuGenerico(err, "Erro ao atualizar", "Não foi possível atualizar o administrador");
    }
  };

  return { administradores, carregando, erro, deletar, criar, editar };
}

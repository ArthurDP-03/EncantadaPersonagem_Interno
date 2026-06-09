import { useState, useEffect } from "react";
import Swal from "sweetalert2";
import {
  getClientes,
  criarCliente,
  atualizarCliente,
  deletarCliente
} from "../services/cliente";

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

export function useClientes() {
  const [clientes, setClientes] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);

  useEffect(() => {
    getClientes()
      .then(setClientes)
      .catch(err => {
        console.error("Erro completo:", err);

        const mensagemErro = obterMensagemErro(err, "Erro ao carregar clientes");
        setErro(mensagemErro);

        Swal.fire({
          icon: "error",
          title: "Erro ao carregar",
          text: mensagemErro
        });
      })
      .finally(() => setCarregando(false));
  }, []);

  const deletar = async (id) => {
    const resultado = await Swal.fire({
      title: "Deseja deletar este cliente?",
      text: "Esta ação não poderá ser desfeita.",
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "Deletar",
      cancelButtonText: "Cancelar",
      confirmButtonColor: "#d33"
    });

    if (!resultado.isConfirmed) return;

    try {
      await deletarCliente(id);

      setClientes(prev => prev.filter(c => c.id !== id));

      Swal.fire({
        icon: "success",
        title: "Cliente deletado",
        timer: 1800,
        showConfirmButton: false
      });
    } catch (err) {
      console.error("Erro ao deletar cliente:", err);

      mostrarErroGenerico(err, "Erro ao deletar", "Não foi possível deletar o cliente");
    }
  };

  const criar = async (dados) => {
    try {
      const novoCliente = await criarCliente(dados);

      setClientes(prev => [...prev, novoCliente]);

      Swal.fire({
        icon: "success",
        title: "Cliente criado",
        timer: 1800,
        showConfirmButton: false
      });
    } catch (err) {
      console.error("Erro ao criar cliente:", err);

      mostrarErroValidacaoOuGenerico(err, "Erro ao criar", "Não foi possível criar o cliente");

      throw err;
    }
  };

  const editar = async (id, dados) => {
    try {
      const atualizado = await atualizarCliente(id, dados);

      setClientes(prev =>
        prev.map(c => (c.id === id ? atualizado : c))
      );

      Swal.fire({
        icon: "success",
        title: "Cliente atualizado",
        timer: 1800,
        showConfirmButton: false
      });
    } catch (err) {
      console.error("Erro ao editar cliente:", err);

      mostrarErroValidacaoOuGenerico(err, "Erro ao atualizar", "Não foi possível atualizar o cliente");

      throw err;
    }
  };

  return {
    clientes,
    carregando,
    erro,
    deletar,
    criar,
    editar
  };
}

import { useState, useEffect } from "react";
import Swal from "sweetalert2";
import {
  getClientes,
  criarCliente,
  atualizarCliente,
  deletarCliente
} from "../services/cliente";

function extrairMensagem(err, fallback = "Ocorreu um erro inesperado.") {
  const d = err?.data;
  if (!d) return fallback;
  if (typeof d.message === "string") return d.message;
  if (typeof d.error   === "string") return d.error;
  if (d.errors && typeof d.errors === "object") {
    return Object.values(d.errors).join(" • ");
  }
  return fallback;
}

function ehErroDeFormulario(err) {
  return err?.status === 400 || err?.status === 422;
}

export function useClientes() {
  const [clientes, setClientes]   = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro]           = useState(null);

  useEffect(() => {
    getClientes()
      .then(setClientes)
      .catch(err => {
        console.error("Erro ao carregar clientes:", err);
        setErro(extrairMensagem(err, "Erro ao carregar clientes."));
        Swal.fire({
          icon: "error",
          title: "Erro ao carregar",
          text: extrairMensagem(err, "Não foi possível carregar os clientes.")
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
      Swal.fire({ icon: "success", title: "Cliente deletado", timer: 1800, showConfirmButton: false });
    } catch (err) {
      console.error("Erro ao deletar cliente:", err);
      Swal.fire({
        icon: "error",
        title: "Erro ao deletar",
        text: extrairMensagem(err, "Não foi possível deletar o cliente.")
      });
    }
  };

  const criar = async (dados) => {
    try {
      const novoCliente = await criarCliente(dados);
      setClientes(prev => [...prev, novoCliente]);
      Swal.fire({ icon: "success", title: "Cliente criado", timer: 1800, showConfirmButton: false });
    } catch (err) {
      console.error("Erro ao criar cliente:", err);

      if (ehErroDeFormulario(err)) {
        throw extrairMensagem(err, "Verifique os campos e tente novamente.");
      }

      Swal.fire({
        icon: "error",
        title: "Erro ao criar",
        text: extrairMensagem(err, "Não foi possível criar o cliente.")
      });
      throw err;
    }
  };

  const editar = async (id, dados) => {
    try {
      const atualizado = await atualizarCliente(id, dados);
      setClientes(prev => prev.map(c => (c.id === id ? atualizado : c)));
      Swal.fire({ icon: "success", title: "Cliente atualizado", timer: 1800, showConfirmButton: false });
    } catch (err) {
      console.error("Erro ao editar cliente:", err);

      if (ehErroDeFormulario(err)) {
        throw extrairMensagem(err, "Verifique os campos e tente novamente.");
      }

      Swal.fire({
        icon: "error",
        title: "Erro ao atualizar",
        text: extrairMensagem(err, "Não foi possível atualizar o cliente.")
      });
      throw err;
    }
  };

  return { clientes, carregando, erro, deletar, criar, editar };
}
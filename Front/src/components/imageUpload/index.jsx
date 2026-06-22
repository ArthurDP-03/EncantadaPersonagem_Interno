import { useEffect, useRef, useState } from "react";
import { Loader2, RefreshCcw, Trash2, Upload } from "lucide-react";
import { useTranslation } from "react-i18next";
import { uploadImagem } from "../../services/uploadService";
import "./index.css";

const ALLOWED_TYPES = new Set(["image/jpeg", "image/png", "image/webp"]);
const MAX_FILE_SIZE = 5 * 1024 * 1024;

const getUploadErrorMessage = (error, fallback) =>
  error?.data?.message || error?.message || fallback;

function ImageUpload({
  value = "",
  onChange,
  disabled = false,
  onUploadingChange = () => {},
}) {
  const { t } = useTranslation();
  const inputRef = useRef(null);
  const objectUrlRef = useRef(null);
  const [localPreview, setLocalPreview] = useState("");
  const [loading, setLoading] = useState(false);
  const [dragActive, setDragActive] = useState(false);
  const [error, setError] = useState("");
  const previewUrl = loading ? localPreview : (value || localPreview);

  const revokeObjectUrl = () => {
    if (objectUrlRef.current) {
      URL.revokeObjectURL(objectUrlRef.current);
      objectUrlRef.current = null;
    }
  };

  const resetInput = () => {
    if (inputRef.current) {
      inputRef.current.value = "";
    }
  };

  useEffect(() => {
    return () => {
      onUploadingChange(false);
      revokeObjectUrl();
    };
  }, [onUploadingChange]);

  const validarArquivo = (file) => {
    if (!file) {
      return t("common.upload.errors.required");
    }

    if (!ALLOWED_TYPES.has(file.type)) {
      return t("common.upload.errors.type");
    }

    if (file.size > MAX_FILE_SIZE) {
      return t("common.upload.errors.size");
    }

    return null;
  };

  const processarArquivo = async (file) => {
    const validationError = validarArquivo(file);

    if (validationError) {
      setError(validationError);
      return;
    }

    setError("");
    revokeObjectUrl();

    const localPreview = URL.createObjectURL(file);
    objectUrlRef.current = localPreview;
    setLocalPreview(localPreview);
    onUploadingChange(true);
    setLoading(true);

    try {
      const url = await uploadImagem(file);
      revokeObjectUrl();
      setLocalPreview(url);
      onChange(url);
    } catch (uploadError) {
      setError(
        getUploadErrorMessage(uploadError, t("common.upload.errors.generic"))
      );
      setLocalPreview(value || "");
      revokeObjectUrl();
    } finally {
      setLoading(false);
      onUploadingChange(false);
      resetInput();
    }
  };

  const handleInputChange = async (event) => {
    const file = event.target.files?.[0];
    if (file) {
      await processarArquivo(file);
    }
  };

  const handleDrop = async (event) => {
    event.preventDefault();
    event.stopPropagation();
    setDragActive(false);

    if (disabled || loading) {
      return;
    }

    const file = event.dataTransfer.files?.[0];
    if (file) {
      await processarArquivo(file);
    }
  };

  const abrirSeletor = () => {
    if (disabled || loading) {
      return;
    }

    inputRef.current?.click();
  };

  const removerImagem = (event) => {
    event.stopPropagation();

    if (disabled || loading) {
      return;
    }

    revokeObjectUrl();
    setLocalPreview("");
    setError("");
    onChange("");
    resetInput();
  };

  const hasPreview = Boolean(previewUrl);

  return (
    <div className="image-upload">
      <div className="image-upload__header">
        <span className="image-upload__label">{t("common.upload.label")}</span>
        <span className="image-upload__hint">{t("common.upload.hint")}</span>
      </div>

      <div
        className={`image-upload__dropzone ${dragActive ? "is-dragging" : ""} ${
          loading ? "is-loading" : ""
        } ${error ? "has-error" : ""}`}
        role="button"
        tabIndex={disabled ? -1 : 0}
        aria-disabled={disabled}
        onClick={abrirSeletor}
        onKeyDown={(event) => {
          if (disabled || loading) {
            return;
          }

          if (event.key === "Enter" || event.key === " ") {
            event.preventDefault();
            abrirSeletor();
          }
        }}
        onDragOver={(event) => {
          event.preventDefault();
          if (!disabled) {
            setDragActive(true);
          }
        }}
        onDragEnter={(event) => {
          event.preventDefault();
          if (!disabled) {
            setDragActive(true);
          }
        }}
        onDragLeave={(event) => {
          event.preventDefault();
          setDragActive(false);
        }}
        onDrop={handleDrop}
      >
        <input
          ref={inputRef}
          className="image-upload__input"
          type="file"
          accept="image/jpeg,image/png,image/webp"
          onChange={handleInputChange}
          disabled={disabled || loading}
        />

        {hasPreview ? (
          <img className="image-upload__preview" src={previewUrl} alt={t("common.upload.previewAlt")} />
        ) : (
          <div className="image-upload__empty">
            <span className="image-upload__icon">
              <Upload size={28} />
            </span>
            <strong>{t("common.upload.emptyTitle")}</strong>
            <p>{t("common.upload.emptyText")}</p>
            <small>{t("common.upload.formats")}</small>
          </div>
        )}

        {loading && (
          <div className="image-upload__loading" aria-live="polite">
            <Loader2 className="image-upload__spinner" size={28} />
            <span>{t("common.upload.loading")}</span>
          </div>
        )}

        <div className="image-upload__actions" onClick={(event) => event.stopPropagation()}>
          <button
            type="button"
            className="image-upload__action"
            onClick={(event) => {
              event.stopPropagation();
              abrirSeletor();
            }}
            disabled={disabled || loading}
          >
            <RefreshCcw size={14} />
            <span>{hasPreview ? t("common.upload.change") : t("common.upload.select")}</span>
          </button>

          {hasPreview && (
            <button
              type="button"
              className="image-upload__action is-danger"
              onClick={removerImagem}
              disabled={disabled || loading}
            >
              <Trash2 size={14} />
              <span>{t("common.upload.remove")}</span>
            </button>
          )}
        </div>
      </div>

      {error && <p className="image-upload__error">{error}</p>}
    </div>
  );
}

export default ImageUpload;

/**
 * 
 */
package org.funsoft.remoteagent.installer.os.dto;

import java.util.List;

/**
 * @author htb
 *
 */
public class ProcessListResultDto {
	private final List<ProcessDto> processes;
	private final String errorMessage;
	public ProcessListResultDto(String errorMessage) {
		this.errorMessage = errorMessage;
		this.processes = null;
	}
	public ProcessListResultDto(List<ProcessDto> processes) {
		this.processes = processes;
		this.errorMessage = null;
	}
	public List<ProcessDto> getProcesses() {
		return processes;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
}
